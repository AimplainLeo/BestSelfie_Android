package shadasviar.bestselfie.PictureStorage.DataProviders;

import java.io.File;
import java.util.List;

import shadasviar.bestselfie.PictureStorage.HistoryManager.HistoryManager;
import shadasviar.bestselfie.PictureStorage.HistoryManager.IHistoryManager;

/**
 * Created by vlad on 8/6/16.
 * @author Shadasviar
 * This class gets Type of history in the constructor and builds actual data as all data excludes
 * given history.
 *
 */
public class BasedOnHistoryData implements IDataStorage {

    private IHistoryManager.HistoryTypes currentType;


    public BasedOnHistoryData(IHistoryManager.HistoryTypes type){
        currentType = type;
    }


    @Override
    public List<File> getActualData() {

        List<File> result;
        IDataStorage ad = new HistoryData(IHistoryManager.HistoryTypes.USEFUL);
        result = ad.getActualData();

        HistoryManager historyManager = HistoryManager.getInstance();
        result.removeAll(historyManager.getHistory(currentType));

        return result;
    }

}
