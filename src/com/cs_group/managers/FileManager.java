package com.cs_group.managers;

import com.cs_group.objects.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * Class:  com.cs_group.managers.FileManager
 * Description: Manager class that handles the entire File portion of the backend
 *              File i/o for Users, File i/o for GameLists
 *              Planned on Singleton Design Pattern, but unsure how in Java
 */
public class FileManager {

    private String m_loginFiles = "login.json";
    private String m_gameFile = "gameFile.json"; // changed per naming convention (?)
    private String m_currentUser = "Users/"; // com.cs_group.objects.User dir
    GameList m_gameListFromFile = new GameList();
    GameList m_SearchResult = new GameList();

    /**
     * constructor: com.cs_group.managers.FileManager()
     *              Upon creation, loads all games from the game file into memory
     * @throws IOException
     * @throws ParseException
     */
    public FileManager() throws IOException, ParseException {
        this.fillGameList(m_gameListFromFile, m_gameFile);
    }



    /**
     * Method: isRegisteredUser:
     * Description: Upon user creation, the user is stored in the login file. This method checks to
     *              ensure no duplicate user is created
     * @param insertUser: com.cs_group.objects.User to check duplicates
     * @return returns TRUE if  the user is found within the file, returns FALSE otherwise.
     * @throws IOException
     * @throws ParseException
     * @throws FileNotFoundException
     */
    public boolean isRegisteredUser(User insertUser) throws IOException, ParseException, FileNotFoundException {
        File m_testFile = new File(m_loginFiles);
        if(m_testFile.length() == 0) { // if the file is empty, returns false.
            return false;
        }
        JSONParser m_parser = new JSONParser(); //creates a new JSON parser that allows the function to parse through the JSON file
        Reader m_reader = new FileReader(m_loginFiles);// creates a new Reader Object that allows the parser to read the information
        JSONObject m_objJSON = (JSONObject)m_parser.parse(m_reader); // JSON object that allows the parser to take in the information from the parser

        JSONArray m_jsonArray = (JSONArray) m_objJSON.get("Users"); //JSON Array that holds the array of users within the file.

        //Creates a String iterator that takes  the JSON iterator to go through the JSON array

        for( int i=0; i<m_jsonArray.size(); i++){
            JSONObject m_jsonObject = new JSONObject();
            m_jsonObject = (JSONObject) m_jsonArray.get(i);
            String m_name = (String) m_jsonObject.get("ID");
            String m_pass = (String) m_jsonObject.get("Password");
            if(m_name.equals(insertUser.getName()) && m_pass.equals(insertUser.getPassword())){
                return true;
            }

        }
        return false;

    }

    /**
     * Method: addUser
     * Description: Loads login file uses into json array if it exists. Then adds user to store back into
     *              this array to store additional user into login file.
     * @param user :  com.cs_group.objects.User to add to file
     * @throws IOException
     * @throws ParseException
     */
    public int addUser(User user) throws IOException, ParseException {
        // deleted some of the comments b/c they were not correct and was confusing me

        JSONObject m_topLevelJson = new JSONObject(); //creates a new JSON object
        JSONArray m_jsonArray; //intializes a new JSONArray

        File m_testFile = new File(m_loginFiles);
        if(m_testFile.length() == 0) { //if the file is empty
            m_jsonArray = new JSONArray(); //Creates the new JSON array
        }
        else{
            JSONParser m_parser = new JSONParser(); //creates a new JSONparser that allows the JSON file to be parsed through for information
            Reader m_reader = new FileReader(m_loginFiles); //creates a new Reader Object that reads in the JSON file
            JSONObject m_objJSON = (JSONObject)m_parser.parse(m_reader); // parses the JSON file and puts the information in a JSONObject
            m_jsonArray = (JSONArray) m_objJSON.get("Users"); // takes the JSON array and puts the com.cs_group.objects.User information within
        }

        // error checking to see if user already exists in file
        for( int i=0; i<m_jsonArray.size(); i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject = (JSONObject) m_jsonArray.get(i);
            String m_name = (String) jsonObject.get("ID");
            if(m_name.equals(user.getName())){
                return 1;
            }
        }

        JSONObject m_jsonObject = new JSONObject();
        m_jsonObject.put("ID", user.getName());
        m_jsonObject.put("Password", user.getPassword());

        m_jsonArray.add(m_jsonObject);
        m_topLevelJson.put("Users", m_jsonArray); // puts the JSON array within the JSON Object

        try {
            FileWriter m_fileToWrite = new FileWriter(m_loginFiles); // creates a new FileWriter Object that uses the loginfiles as the filepath
            m_fileToWrite.write(m_topLevelJson.toJSONString()); //writes a JSON object to the file
            m_fileToWrite.flush(); //flushes the file stream
            m_fileToWrite.close();//closes the filestream
            this.saveUserData(user);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Method: isGameinList
     *          Uses the com.cs_group.objects.Game list that was loaded from the big game file to check if game exists
     * @param game
     * @return true is exists, false if it doesn't
     * @throws IOException
     * @throws ParseException
     */
    public boolean isGameInList(Game game) throws IOException, ParseException {
        for (Game g : (Iterable<Game>) m_gameListFromFile) {  // For loop that loops through each item in a JSON Array
            if (g.compareNames(game)) { // If the current iterator equals the information in the JSON file, returns true.
                return true;
            }
        }
        return false;
    }

    /**
     * method: gamesSearchResult()
     *         method that handles game searching. Creates a Gamelist of all the games that match the serach
     *         results, then returns it. Searching is done through compareNames() method from com.cs_group.objects.Game class
     * @param game A game that contains the string from the user's search as the game title to compare
     * @return Gamelist that holds all games with the search string somewhere in the game
     * @throws IOException
     * @throws ParseException
     */
    public GameList gamesSearchResult(Game game) throws IOException, ParseException {
        GameList m_gameList = new GameList();

        for (Game g : (Iterable<Game>) m_gameListFromFile) {  // For loop that loops through each item in a JSON Array
            if (g.compareNames(game)) { // If the current iterator equals the information in the JSON file, returns true.
                m_gameList.addGame(g);
            }
        }
        return m_gameList;
    }

    /**
     * method: fillGameList()
     *          gets the games that are from the gamefile.json are loads into memory
     * @param g Gamelist to populate
     * @param fileName name of the gamefile
     * @throws IOException
     * @throws ParseException
     */
    public void fillGameList(GameList g, String fileName) throws IOException, ParseException {
        if(g.getLength() != 0) {
            System.err.println("gameList populated");
            return;
        }
        JSONParser m_parser = new JSONParser(); //creates a new JSON parser that allows the function to parse through the JSON file
        Reader m_reader = new FileReader(fileName);// creates a new Reader Object that allows the parser to read the information
        JSONObject m_objJSON = (JSONObject)m_parser.parse(m_reader); // JSON object that allows the parser to take in the information from the parser

        File m_testFile = new File(fileName);
        if(m_testFile.length() == 0) { // if the file is empty, returns false.
            System.err.println("file not found");
            return;
        }

        JSONArray m_jsonArray = (JSONArray) m_objJSON.get("Games"); //JSON Array that holds the array of users within the file.

        // maybe a better way, works... look into later
        for(int i =0; i<m_jsonArray.size(); i++){
            JSONObject m_jsonObject = (JSONObject) m_jsonArray.get(i);
            Game m_tmpGame = new Game();//Creates a new com.cs_group.objects.Game object so that data can be sent to another class
            m_tmpGame.setId((String) m_jsonObject.get("ID"));
            m_tmpGame.setTitle((String) m_jsonObject.get("Title"));
            m_tmpGame.setHighlights((String) m_jsonObject.get("Highlights Supported?"));
            m_tmpGame.setOptimized((String) m_jsonObject.get("Fully Optimized?"));
            m_tmpGame.setURL((String) m_jsonObject.get("Steam Url"));
            m_tmpGame.setPublisher((String) m_jsonObject.get("Publisher"));
            m_tmpGame.setGenre((String) m_jsonObject.get("Genre"));
            m_tmpGame.setStatus((String) m_jsonObject.get("Status"));

            g.addGame(m_tmpGame);
        }


    }

    /**
     * method: saveUserData
     *         saves the gamelist owned by the user into the respective user/[username].json file.
     *         Stores games and users with JSON arrays and JSON objects
     * @param user com.cs_group.objects.User that is stored into their file
     * @throws IOException
     * @throws ParseException
     */
    public void saveUserData(User user) throws IOException, ParseException {
        String m_userFile = m_currentUser.concat(user.getName());
        m_userFile = m_userFile.concat(".json");
        // deleted some of the comments b/c they were not correct and was confusing me

        JSONObject m_topLevelJson = new JSONObject(); // top level is the main object to push

        JSONObject m_jsonObjectUser = new JSONObject(); // stores username
        JSONObject m_jsonObjectUserGames = new JSONObject();
        JSONArray m_jsonArrayGameLists; // stores gameLists
        JSONArray m_jsonArrayGames;

        m_jsonArrayGames = new JSONArray();
        m_jsonArrayGameLists = new JSONArray();

        for (GameList gl : (Iterable<GameList>) user) {  // For loop that loops through each item in a JSON Array
            // went back to fix this. JSON needs these to be able to fetch later
            for (Game g : (Iterable<Game>) gl){
                JSONObject m_jsonObjectGame = new JSONObject();
                m_jsonObjectGame.put("ID", g.getID());
                m_jsonObjectGame.put("Title", g.getTitle());
                m_jsonObjectGame.put("Highlights Supported?", g.getHighlights());
                m_jsonObjectGame.put("Fully Optimized?", g.getOptimized());
                m_jsonObjectGame.put("Steam Url", g.getSteamUrl());
                m_jsonObjectGame.put("Publisher", g.getPublisher());
                m_jsonObjectGame.put("Genre", g.getGenre());
                m_jsonObjectGame.put("Status", g.getStatus());
                m_jsonObjectGame.put("Rating", g.getRating());
                m_jsonArrayGames.add(m_jsonObjectGame);
            }
            m_jsonObjectUser.put("GameListName", gl.getListName());
            m_jsonObjectUser.put("Games", m_jsonArrayGames);
            m_jsonArrayGameLists.add(m_jsonObjectUser);
        }

        m_topLevelJson.put("Name", user.getName());
        m_jsonObjectUserGames.put("GameLists", m_jsonArrayGameLists);

        m_topLevelJson.put("Data",m_jsonObjectUserGames); // puts the JSON array within the JSON Object

        try {
            FileWriter m_fileToWrite = new FileWriter(m_userFile); // creates a new FileWriter Object that uses the loginfiles as the filepath
            m_fileToWrite.write(m_topLevelJson.toJSONString()); //writes a JSON object to the file
            m_fileToWrite.flush(); //flushes the file stream
            m_fileToWrite.close();//closes the filestream
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * method: loadUser
     *          assumes that multiple gamelists are not implemented. Fetches the user
     *          file to get each jsonObject.get(). Each is stored into gamelist(0)
     * @param user com.cs_group.objects.User to load the gamelist into
     * @throws IOException
     * @throws ParseException
     */
    public void loadUser(User user) throws IOException, ParseException {
        user.getGameLists().get(0).clear();
        String m_userFile = m_currentUser.concat(user.getName());
        m_userFile = m_userFile.concat(".json");
        // deleted some of the comments b/c they were not correct and was confusing me

        File m_testFile = new File(m_userFile);

        if(m_testFile.length() == 0) { // if the file is empty, returns false.
            System.err.println("file not found");
            return;
        }
        JSONParser m_parser = new JSONParser(); //creates a new JSON parser that allows the function to parse through the JSON file
        Reader m_reader = new FileReader(m_userFile);// creates a new Reader Object that allows the parser to read the information
        JSONObject m_objJSON = (JSONObject)m_parser.parse(m_reader); // JSON object that allows the parser to take in the information from the parser

        JSONObject m_objJSONData = (JSONObject) m_objJSON.get("Data");
        JSONArray m_objJSONGameLists = (JSONArray) m_objJSONData.get("GameLists");

        for(int i=0;i<m_objJSONGameLists.size();i++){
            JSONObject m_objJSONGame = (JSONObject) m_objJSONGameLists.get(i);
            JSONArray m_jsonArrayData = (JSONArray) m_objJSONGame.get("Games");

            for(int j =0; j<m_jsonArrayData.size(); j++){
                JSONObject m_jsonObject = (JSONObject) m_jsonArrayData.get(j);
                Game m_tmpGame = new Game();//Creates a new com.cs_group.objects.Game object so that data can be sent to another class
                m_tmpGame.setId((String) m_jsonObject.get("ID"));
                m_tmpGame.setTitle((String) m_jsonObject.get("Title"));
                m_tmpGame.setHighlights((String) m_jsonObject.get("Highlights Supported?"));
                m_tmpGame.setOptimized((String) m_jsonObject.get("Fully Optimized?"));
                m_tmpGame.setURL((String) m_jsonObject.get("Steam Url"));
                m_tmpGame.setPublisher((String) m_jsonObject.get("Publisher"));
                m_tmpGame.setGenre((String) m_jsonObject.get("Genre"));
                m_tmpGame.setStatus((String) m_jsonObject.get("Status"));
                m_tmpGame.setRating((String) m_jsonObject.get("Rating"));
                user.getGameLists().get(0).addGame(m_tmpGame);
            }
        }
    }

}