package deneyimkutusu.xedoxsoft.deneyimkutusu.Model;

public class LikeModel {
    String post_id;
    String kul_id;
    String ad_soyad;

    public LikeModel(){
    }
    public LikeModel(String post_id, String kul_id, String ad_soyad) {

        this.post_id = post_id;
        this.kul_id = kul_id;
        this.ad_soyad = ad_soyad;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getKul_id() {
        return kul_id;
    }

    public void setKul_id(String kul_id) {
        this.kul_id = kul_id;
    }

    public String getAd_soyad() {
        return ad_soyad;
    }

    public void setAd_soyad(String ad_soyad) {
        this.ad_soyad = ad_soyad;
    }
}
