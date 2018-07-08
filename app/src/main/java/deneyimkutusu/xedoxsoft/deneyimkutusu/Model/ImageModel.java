package deneyimkutusu.xedoxsoft.deneyimkutusu.Model;

/**
 * Created by Erdem Gençoğlu on 1.03.2018.
 */

public class ImageModel {

    String imageName;
    String imageUrl;
    String name;

    public ImageModel(String imageName,String imageUrl,String name){
        this.imageName=imageName;
        this.imageUrl=imageUrl;
        this.name=name;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
