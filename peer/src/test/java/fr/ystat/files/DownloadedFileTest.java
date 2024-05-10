package fr.ystat.files;

import fr.ystat.utils.MockUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

public class DownloadedFileTest {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception{
        MockUtils.mockMain();
    }

    // Weird test case, trigger it manually by added a @Test, remove origin.txt from download folder before doing so.
    @SneakyThrows
    public static void testPartitionedToComplete(){
        String fileContent = "Hello, this is the content of a file, you should not spend so much time reading it, " +
                "but you are excused if you are just checking that everything went right after being processed." +
                "The processing consist of splitting this file into multiples ones, and then trying to put them back together";

        File originFile = new File("origin.txt");
        originFile.deleteOnExit();
        Files.write(originFile.toPath(), fileContent.getBytes());

        CompletedFile completedFile = CompletedFile.fromLocalFile(originFile, 1024L);
        DownloadedFile downloadedFile = new DownloadedFile(completedFile.getProperties());
        for (int i = 0; i < completedFile.getBitSet().getLength(); ++i){
            downloadedFile.addPartition(i, completedFile.getPartition(i));
        }

        downloadedFile.getParentFolder().deleteOnExit();

    }
}
