package shadasviar.bestselfie.PictureStorage.HistoryManager;

import java.util.List;
import java.util.Set;

/**
 * Created by vlad on 8/6/16.
 * @author Shadasviar
 * This interface describes class, which store some types of history, described in the HistoryTypes
 * enum.
 */
public interface IHistoryManager {

    /*You can change this enum for you own types of history.*/
    enum HistoryTypes{
        VIEWED,
        SENT,
        USEFUL, /* useful means data with  faces*/
        CACHE
    }

    Set<? extends Object> getHistory(HistoryTypes historyID);
    void addToHistory(HistoryTypes historyID, List<? extends Object> data);
    void clearHistory(HistoryTypes historyID);
}
