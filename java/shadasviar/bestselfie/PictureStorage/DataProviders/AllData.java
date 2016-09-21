package shadasviar.bestselfie.PictureStorage.DataProviders;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Shadasviar
 * This class scan DCIM directory on android and returns all pictures
 * (formats of pictures are in the
 * @see #fileTypes) from this dir and subdirs
 */
public class AllData implements IDataStorage{

    private List<File> pictures;
    private static List<String> fileTypes = Arrays.asList("jpeg", "jpg", "png", "gif");


    public AllData() {
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        pictures = new ArrayList<>();
        traverseDir(f);
    }

    @Override
    public List<File> getActualData() {
        return pictures;
    }


    private void traverseDir(File currentDir){
        File [] paths = currentDir.listFiles();
        for(File f : paths){
            if(f.isDirectory()){
                traverseDir(f);
            }
        }
        pickPictures(currentDir);
    }


    private void pickPictures (File data){

        FilenameFilter filter = new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name){
                if(name.lastIndexOf('.') > 0){
                    int lastDot = name.lastIndexOf('.') + 1;

                    String str = name.substring(lastDot);
                    if(fileTypes.contains(str.toLowerCase())){
                        return true;
                    }

                }
                return false;
            }
        };

        File[] paths = data.listFiles(filter);

        try{
            for(File f: paths){
                pictures.add(f);
            }
        }catch(Exception ex){
            System.out.println("Exception");
        }
    }
}
