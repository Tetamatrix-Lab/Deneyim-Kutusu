package deneyimkutusu.xedoxsoft.deneyimkutusu.Model;

/**
 * Created by Erdem Gençoğlu on 7.02.2018.
 */

public class DataClass {

    private String icerik_id;
    private String uye_id;
    private String kategori_name;
    private String surname;
    private String nameSurname;
    private String country;
    private String city;
    private String postDate;
    private String upNumber;
    private String downNumber;
    private String commentNumber;
    private String postTitle;
    private String postExperince;
    private String profileImage;
    private String postImage_url;
    private String postImage_url2;
    private String postLatitude;
    private String postLongitude;
    private String postLocation;

    public DataClass(){
    }
    //Default Constructermız
    public DataClass(String icerik_id, String uye_id, String kategori_name,String surname, String nameSurname, String country,String city, String postDate,
                     String upNumber, String downNumber, String commentNumber,
                     String postTitle, String postExperince, String postImage_url,String postImage_url2, String profileImage,
                     String postLatitude, String postLongitude,String postLocation){
        this.icerik_id=icerik_id;
        this.uye_id=uye_id;
        this.kategori_name = kategori_name;
        this.surname=surname;
        this.nameSurname=nameSurname;
        this.country=country;
        this.city=city;
        this.postDate=postDate;
        this.upNumber=upNumber;
        this.downNumber=downNumber;
        this.commentNumber=commentNumber;
        this.postTitle=postTitle;
        this.postExperince = postExperince;
        this.profileImage=profileImage;
        this.postImage_url = postImage_url;
        this.postImage_url2=postImage_url2;
        this.postLatitude=postLatitude;
        this.postLongitude=postLongitude;
        this.postLocation=postLocation;
    }
    public String getIcerik_id() {
        return icerik_id;
    }

    public void setIcerik_id(String icerik_id) {
        this.icerik_id = icerik_id;
    }

    public String getUye_id() {
        return uye_id;
    }

    public void setUye_id(String uye_id) {
        this.uye_id = uye_id;
    }

    public String getKategori_name() {
        return kategori_name;
    }

    public void setKategori_name(String kategori_name) {
        this.kategori_name = kategori_name;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getUpNumber() {
        return upNumber;
    }

    public void setUpNumber(String upNumber) {
        this.upNumber = upNumber;
    }

    public String getDownNumber() {
        return downNumber;
    }

    public void setDownNumber(String downNumber) {
        this.downNumber = downNumber;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage_url() {
        return postImage_url;
    }

    public void setPostImage_url(String postImage_url) {
        this.postImage_url = postImage_url;
    }

    public String getPostExperince() {
        return postExperince;
    }

    public void setPostExperince(String postExperince) {
        this.postExperince = postExperince;
    }
    public String getPostLatitude() {
        return postLatitude;
    }

    public void setPostLatitude(String postLatitude) {
        this.postLatitude = postLatitude;
    }

    public String getPostLongitude() {
        return postLongitude;
    }

    public void setPostLongitude(String postLongitude) {
        this.postLongitude = postLongitude;
    }

    public String getPostImage_url2() {
        return postImage_url2;
    }

    public void setPostImage_url2(String postImage_url2) {
        this.postImage_url2 = postImage_url2;
    }

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}