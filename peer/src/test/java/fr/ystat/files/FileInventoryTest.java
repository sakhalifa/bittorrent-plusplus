package fr.ystat.files;


import fr.ystat.Main;
import fr.ystat.utils.MockUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FileInventoryTest {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception{
        MockUtils.mockMain();
    }

    // Default test
    @Test
    public void instantiationTest() {
        FileInventory fileInventory =  FileInventory.getInstance();
    }

    @Test
    public void fileAdditionTest() {
        FileInventory fileInventory =  FileInventory.getInstance();
        fileInventory.addStockedFile(new DownloadedFile("File1", 10000, 2000, "fakeHash1"));
        fileInventory.addStockedFile(new DownloadedFile("File2", 100000, 2000, "fakeHash2"));
        fileInventory.addStockedFile(new DownloadedFile("File3", 50000, 2000, "fakeHash3"));
        fileInventory.addStockedFile(new DownloadedFile("File4", 20000, 2000, "fakeHash4"));
    }

  @Test
    public void fileRequestTest() {
        FileInventory fileInventory =  FileInventory.getInstance();
        StockedFile f1 = new DownloadedFile("File1", 10000, 2000, "fakeHash1");
        StockedFile f2 = new DownloadedFile("File2", 100000, 2000, "fakeHash2");
        StockedFile f3 = new DownloadedFile("File3", 50000, 2000, "fakeHash3");
        StockedFile f4 = new DownloadedFile("File4", 20000, 2000, "fakeHash4");
        fileInventory.addStockedFile(f1);
        fileInventory.addStockedFile(f2);
        fileInventory.addStockedFile(f3);
        fileInventory.addStockedFile(f4);

        assert fileInventory.getStockedFile("fakeHash1") == f1;
        assert fileInventory.getStockedFile("fakeHash2") == f2;
        assert fileInventory.getStockedFile("fakeHash3") == f3;
        assert fileInventory.getStockedFile("fakeHash4") == f4;
    }
}
