package shadasviar.bestselfie.PictureSelector;
import java.io.File;
import java.util.List;

/**
 * Created by vlad on 7/30/16.
 */
public interface PictureSelectorInterface {

    List<File> getFilteredPictures(List<File> data);
    /*If it was data to caching, returns true*/
    boolean cacheData(List<File> data);

}
