package deneyimkutusu.xedoxsoft.deneyimkutusu.Model;

/**
 * Created by Erdem Gençoğlu on 16.03.2018.
 */

public class KategoriModel {

    private String kategori_adi;
    private String kategori_id;

    public KategoriModel(){

    }
    public KategoriModel(String kategori_id,String kategori_adi){
        this.kategori_id=kategori_id;
        this.kategori_adi=kategori_adi;
    }
    public String getKategori_adi() {
        return kategori_adi;
    }

    public void setKategori_adi(String kategori_adi) {
        this.kategori_adi = kategori_adi;
    }

    public String getKategori_id() {
        return kategori_id;
    }

    public void setKategori_id(String kategori_id) {
        this.kategori_id = kategori_id;
    }


}
