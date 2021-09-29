package win.woon.textorm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static win.woon.textorm.TextORM.*;

public class Model<T> {
    @Column
    public int id = -1;

    public void save() {
        try {
            Path storageLocation = getRepositoryStorageLocation(this.getClass());
            createFileIfEmpty(storageLocation);

            BufferedReader reader = new BufferedReader(new FileReader(storageLocation.toAbsolutePath().toString()));
            StringBuilder outputBuffer = new StringBuilder();
            String line;

            boolean recordExists = false;
            while ((line = reader.readLine()) != null) {
                // Update existing
                HashMap<String, String> lineMap = SaveString.toHashMap(line);
                if (Objects.equals(lineMap.get("id"), String.valueOf(this.id))) {
                    outputBuffer.append(this.toSaveString()).append(System.lineSeparator());
                    recordExists = true;
                } else {
                    outputBuffer.append(line).append(System.lineSeparator());
                }
            }
            reader.close();

            if (!recordExists) {
                Meta meta = TextORM.readModelMeta(this.getClass());
                if (meta == null) {
                    meta = new Meta(getTableName(this.getClass()), 1);
                }
                this.id = meta.autoIncrement++;
                outputBuffer.append(this.toSaveString()).append(System.lineSeparator());
                meta.save();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(storageLocation.toAbsolutePath().toString()));
            writer.write(outputBuffer.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String toSaveString() {
        ArrayList<String> fieldData = new ArrayList<>();
        this.toHashMap().forEach((s, s2) -> fieldData.add(s + ":" + s2));
        return String.join("|", fieldData);
    }

    private HashMap<String, String> toHashMap() {
        Field[] fields = this.getClass().getFields();
        HashMap<String, String> hashMap = new HashMap<>();
        for (Field field : fields) {
            if (field.getAnnotation(Column.class) != null) {
                try {
                    hashMap.put(field.getName(), field.get(this).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return hashMap;
    }
}
