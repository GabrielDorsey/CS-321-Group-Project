import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class User
 *       The main data struct that is passed around through the panels
 *       Contains every information the User needs to use the software
 */
public class User implements Iterable, Cloneable{
    ArrayList<GameList> m_GameLists = new ArrayList<>(); // Game list placeholder
    GameList m_activeGameList;
    String m_Name;
    String m_Password;

    /**
     * Constructor that inits vars.
     */
    public User(){
        m_Name = "";
        m_Password = "";
        m_activeGameList = new GameList();
        m_GameLists.add(m_activeGameList);
    }

    /**
     * Overloaded contructor with name and password set
     * @param name name for User
     * @param password password for User
     */
    public User(String name, String password){
        m_Name = name;
        m_Password = password;
        m_activeGameList = new GameList();
        m_GameLists.add(m_activeGameList);
    }

    /**
     * Bad design but, didn't know how else. Just returns the arraylists of GameLists
     * @return
     */
    public ArrayList<GameList> getGameLists() {
        return m_GameLists;
    }

    /**
     * Basic Set. Passwords don't have any protection
     * @param password password
     */
    public void setPassword(String password){m_Password = password;}

    /**
     * Basic set
     * @param name Name
     */
    public void setName(String name){m_Name = name;}

    /**
     * Conviences. Basic set for Name & Password
     * @param name name
     * @param password passowrd
     * @return boolean (unsure why i made it a bool)
     */
    public boolean setInfo(String name, String password){
        m_Name = name;
        m_Password = password;
        return true;
    }

    /**
     * Basic get
     * @return name
     */
    public String getName(){return m_Name;} //Changed the gets for right now so that it does not require a String for a parameter for login (3/28/21)

    /**
     * Basic get. Password has no protections
     * @return passowrd
     */
    public String getPassword(){return m_Password;} //Changedthe gets for right now so that it does not require the String for a parameter for login (3/28/21)

    /**
     * basic toString
     * @return string with all information
     */
    @Override
    public String toString() {
        return "User{" +
                "m_Name='" + m_Name + '\'' +
                ", m_Password='" + m_Password + '\'' +
                '}';
    }


    /**
     * to JSON. to string, but repurposed for the JSON
     * @return string for that JSON
     */
    public String toJSON(){
        return "{\"ID\":\""+ "User{" +
                "m_Name='" + m_Name + '\'' +
                ", m_Password='" + m_Password + '\'' +
                "}\"}";
    }


    public void createGameList(GameList gameList){
        m_GameLists.add(gameList);
    }

    public void removeGameList(String gameListName){
    }

    public void addGame(String gameListName, Game game){

    }

    public Iterator<GameList> iterator() {
        return this.m_GameLists.iterator();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

