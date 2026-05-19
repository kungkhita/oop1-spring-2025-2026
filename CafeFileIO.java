package cafemanagementsystem.fileio;
import cafemanagementsystem.entity.Cafe;
import java.io.*;

public class CafeFileIO {
    private static final String FILE_NAME = "cafe.txt";
    private static final String TEMP_FILE = "temp.txt";

    public static void createFileIfNotExists() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists())
            file.createNewFile();
    }

    public static boolean itemIdExists(String itemId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cafe c = Cafe.fromLine(line);
                if (c != null && c.getItemId().equals(itemId))
                    return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    public static int countRecords() {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (Cafe.fromLine(line) != null)
                    count++;
            }
        } catch (IOException ignored) {
        }
        return count;
    }

    public static void addCafe(Cafe c) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            pw.println(c.toLine());
        }
    }

    public static boolean updateCafe(Cafe c) throws IOException {
        File inputFile = new File(FILE_NAME);
        File tempFile = new File(TEMP_FILE);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cafe existing = Cafe.fromLine(line);
                if (existing != null && existing.getItemId().equals(c.getItemId())) {
                    bw.write(c.toLine());
                    found = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
        }
        if (found) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Could not finalize update.");
            }
        } else {
            tempFile.delete();
        }
        return found;
    }

    public static boolean deleteCafe(String itemId) throws IOException {
        File inputFile = new File(FILE_NAME);
        File tempFile = new File(TEMP_FILE);
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cafe existing = Cafe.fromLine(line);
                if (existing != null && existing.getItemId().equals(itemId)) {
                    found = true;
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
        }
        if (found) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Could not finalize delete.");
            }
        } else {
            tempFile.delete();
        }
        return found;
    }

    public static Object[][] getAllCafes() {
        int total = countRecords();
        Object[][] rows = new Object[total][4];
        int idx = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null && idx < total) {
                Cafe c = Cafe.fromLine(line);
                if (c != null) {
                    Object[] row = c.toRow();
                    rows[idx][0] = row[0];
                    rows[idx][1] = row[1];
                    rows[idx][2] = row[2];
                    rows[idx][3] = row[3];
                    idx++;
                }
            }
        } catch (IOException ignored) {
        }
        return rows;
    }

    public static Object[][] searchCafes(String keyword) {
        String kw = keyword.toLowerCase();
        int matchCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Cafe c = Cafe.fromLine(line);
                if (c != null && (c.getItemId().toLowerCase().contains(kw)
                        || c.getItemName().toLowerCase().contains(kw))) {
                    matchCount++;
                }
            }
        } catch (IOException ignored) {
        }

        Object[][] results = new Object[matchCount][4];
        int idx = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null && idx < matchCount) {
                Cafe c = Cafe.fromLine(line);
                if (c != null && (c.getItemId().toLowerCase().contains(kw)
                        || c.getItemName().toLowerCase().contains(kw))) {
                    Object[] row = c.toRow();
                    results[idx][0] = row[0];
                    results[idx][1] = row[1];
                    results[idx][2] = row[2];
                    results[idx][3] = row[3];
                    idx++;
                }
            }
        } catch (IOException ignored) {
        }
        return results;
    }
}
