package shadasviar.bestselfie.PictureStorage;

import shadasviar.bestselfie.PictureStorage.DataProviders.IDataStorage;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;

/**
 * Created by vlad on 8/8/16.
 * @author Shadasviar
 *
 * It describes interface from dataStorage to client, includes configuration of iys behaviour
 */
public interface IDataStorageManager extends IDataStorage {
    void setAllData();
    void setBasedOnHistoryData(IHistoryManager.HistoryTypes historyType);
    void setHistoryData(IHistoryManager.HistoryTypes historyType);
}
