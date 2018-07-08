package deneyimkutusu.xedoxsoft.deneyimkutusu.Model;

public class YorumModel {
    String yorum_id;
    String post_id;
    String yorum;
    String uye_id;
    String kisi_resim_url;
    String uye_adi;
    public YorumModel(){

    }
    public YorumModel(String yorum_id, String post_id, String yorum, String uye_id, String kisi_resim_url,String uye_adi){
        this.yorum_id=yorum_id;
        this.post_id=post_id;
        this.yorum=yorum;
        this.uye_id = uye_id;
        this.kisi_resim_url=kisi_resim_url;
        this.uye_adi=uye_adi;
    }

    public String getYorum_id() {
        return yorum_id;
    }

    public void setYorum_id(String yorum_id) {
        this.yorum_id = yorum_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getUye_id() {
        return uye_id;
    }

    public void setUye_id(String uye_id) {
        this.uye_id = uye_id;
    }

    public String getKisi_resim_url() {
        return kisi_resim_url;
    }

    public void setKisi_resim_url(String kisi_resim_url) {
        this.kisi_resim_url = kisi_resim_url;
    }

    public String getUye_adi() {
        return uye_adi;
    }

    public void setUye_adi(String uye_adi) {
        this.uye_adi = uye_adi;
    }

}
