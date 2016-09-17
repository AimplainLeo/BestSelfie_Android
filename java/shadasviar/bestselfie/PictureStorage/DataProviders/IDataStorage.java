package shadasviar.bestselfie.PictureStorage.DataProviders;

import java.io.File;
import java.util.List;

/**
 * Created by vlad on 8/4/16.
 */
public interface IDataStorage {

    List<File> getActualData();
    /*TODO: implement curent session that don't hide pictures shared on current session*/

}
