package group3.myapplicationlab2;

/**
 * Created by mc on 26/04/17.
 */

public class User {
    //private String ID;
    private String email;
    //private String password;

    public User (){

    }

    /*public User (String email, String password){
        this.email = email;
        this.password = password;
    }*/

    //public void setPassword(String password){
    //    /*check the case of internal spaces/blank/invalid chars */
    //    if (password.trim().length() >= 6)
    //        this.password = password.trim();
    //    return;
    //}

    //public String getPassword(){return password;}

    /*public void setEmail(String email){
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            this.email = email;
            return;
        }
    }*/

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){return email;}

    //public void setID(String ID){this.ID = ID;}
    //public String getId(){return this.ID;}

}
