package deneyimkutusu.xedoxsoft.deneyimkutusu.Model;

/**
 * Created by Erdem Gençoğlu on 28.02.2018.
 */

public class UserModel {

    private String resimAdi;
    private String resimUrl;
    private String kapakUrl="https://firebasestorage.googleapis.com/v0/b/deneyim-kutusu.appspot.com/o/kapak%20resmi.png?alt=media&token=20d4dbc1-3c11-4922-9d2d-4be712ae5e9a";
    private String kulId;//user_id
    private String isim;
    private String soyisim;
    private String ulke;
    private String sehir;
    private String rutbe;
    private String inceleme_sayisi;

    public UserModel(){
        //Default Constructerımız Datasnapshot.getValue(UserModel.class) seklinde cagırılması için
    }
    public UserModel(String resimAdi, String resimUrl,String kapakUrl, String kulId, String isim, String soyisim, String ulke, String sehir,String rutbe,String inceleme_sayisi) {
        this.resimAdi = resimAdi;
        this.resimUrl=resimUrl;
        this.kapakUrl=kapakUrl;
        this.kulId=kulId;
        this.isim=isim;
        this.soyisim=soyisim;
        this.ulke=ulke;
        this.sehir=sehir;
        this.rutbe=rutbe;
        this.inceleme_sayisi = inceleme_sayisi;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public void setSoyisim(String soyisim) {
        this.soyisim = soyisim;
    }
    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }
    public String getUlke() {
        return ulke;
    }

    public void setUlke(String ulke) {
        this.ulke = ulke;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getKulId() {
        return kulId;
    }

    public void setKulId(String kulId) {
        this.kulId = kulId;
    }
    public String getResimUrl() {
        return resimUrl;
    }

    public void setResimUrl(String resimUrl) {
        this.resimUrl = resimUrl;
    }
    public String getResimAdi() {
        return resimAdi;
    }

    public void setResimAdi(String resimAdi) {
        this.resimAdi = resimAdi;
    }
    public String getKapakUrl() {
        return kapakUrl;
    }

    public void setKapakUrl(String kapakUrl) {
        this.kapakUrl = kapakUrl;
    }

    public String getRutbe() {
        return rutbe;
    }

    public void setRutbe(String rutbe) {
        this.rutbe = rutbe;
    }

    public String getInceleme_sayisi() {
        return inceleme_sayisi;
    }

    public void setInceleme_sayisi(String inceleme_sayisi) {
        this.inceleme_sayisi = inceleme_sayisi;
    }

}
