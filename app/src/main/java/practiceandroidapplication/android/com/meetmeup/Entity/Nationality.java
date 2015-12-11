package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 07/12/2015.
 */
public class Nationality {

    private int id;
    private String nationality;

    /*
        constructors
     */

    public Nationality() {

    }

    public Nationality(int id, String nationality) {
        this.id = id;
        this.nationality = nationality;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationality(){
        return nationality;
    }

}
