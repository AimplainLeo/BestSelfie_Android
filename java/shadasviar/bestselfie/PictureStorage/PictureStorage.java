package shadasviar.bestselfie.PictureStorage;


import java.io.File;
import java.util.List;

import shadasviar.bestselfie.PictureStorage.DataProviders.AllData;
import shadasviar.bestselfie.PictureStorage.DataProviders.BasedOnHistoryData;
import shadasviar.bestselfie.PictureStorage.DataProviders.HistoryData;
import shadasviar.bestselfie.PictureStorage.DataProviders.IDataStorage;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;

/**
 * Created by vlad on 8/4/16.
 * @author Shadasviar
 */
 public class PictureStorage implements IDataStorageManager {

    private IDataStorage currentDataSrc;

    public PictureStorage(){
        currentDataSrc = new BasedOnHistoryData(IHistoryManager.HistoryTypes.SENT);
    }


    @Override
    public List<File> getActualData() {
        return currentDataSrc.getActualData();
    }


    @Override
    public void setAllData() {
        currentDataSrc = new AllData();
    }


    @Override
    public void setBasedOnHistoryData(IHistoryManager.HistoryTypes historyType) {
        currentDataSrc = new BasedOnHistoryData(historyType);
    }


    @Override
    public void setHistoryData(IHistoryManager.HistoryTypes historyType) {
        currentDataSrc = new HistoryData(historyType);
    }
}
