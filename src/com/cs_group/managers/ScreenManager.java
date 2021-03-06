package com.cs_group.managers;

import com.cs_group.objects.*;
import com.cs_group.panels.*;
import com.cs_group.managers.FileManager;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;


/**
 * Class: com.cs_group.managers.ScreenManager:
 * Description: Manages all of the screens, and uses a card dispay so that each screen can be shown with button clicks.
 * This class uses the Observer pattern so that it can listen to any changes that occur throughout the panels, so that this class can be notified and the right switch can be made.
 *
 */
public class ScreenManager {

    public ScreenManager(){}

    public static void main(String[] args) throws IOException, ParseException {
        JFrame mainFrame = new JFrame(); //creates a new Jframe
        FileManager m_fileManager = new FileManager();
        User m_user = new User();
        //sets the format of the Jframe
        mainFrame.setSize(1200,750); //sets the Jframe size
        mainFrame.setLocationRelativeTo(null); // sets the location to null so that the Jframe does not open in the top left corner of the screen


        final LoginScreenDisplay userLogin = new LoginScreenDisplay(m_user); //creates a new loginDisplay for the card Layout
        final RegisterUserDisplay userRegister = new RegisterUserDisplay(); // creates a new Register user display for the card Layout;
        final searchResultsDisplay searchResults = new searchResultsDisplay(m_user); //   creates a new testDisplay
        final UserpageDisplay userListPage = new UserpageDisplay(m_user); // Creates a new userpage display
        final JPanel cardSet= new JPanel(new CardLayout());       //creates a card set which will accept Jpanels into its cardset;

        cardSet.add(userLogin.getLoginPanel(), "LoginView"); //adds the login display Panel
        cardSet.add(userRegister.getRegisterDisplay(), "RegisterView"); //adds the Regiser Display
        cardSet.add(userListPage.getUserListPanel(),"UserView"); //adds the userpage display
        cardSet.add(searchResults.getSearchResultPanel(),"Search Results"); //adds the searchResult Display
        mainFrame.add(cardSet);  //adds  cardset to the JFrame


        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        CardLayout cl = (CardLayout) (cardSet.getLayout());
    /*
      Change listener that listens for a change to happen in the userLogin(LoginScreenDisplay) panel, which will be when the Log in button is clicked
     */

        userLogin.addLoginUserListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                userListPage.createTable(m_user);
                userListPage.setUserTitle(m_user.getName() + "'s Game Diary");
                cl.show(cardSet,  "UserView");// shows the userpage card
            }

        });
        //ChangeListener that listens for the registerButton to be clicked and switches the view to RegisterUserDisplay
        userLogin.addRegisterUserListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent  e2){
                mainFrame.setSize(500,500);
                cl.show(cardSet,  "RegisterView"); //shows the the registerUser Card
            }

        });

        //changeListener in UserpageDisplay that listens to registerUser button and returns the user to the loginScreen
        userRegister.addListenerReturntoLogin(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                mainFrame.setSize(1200,750);
                String event = changeEvent.getSource().toString();
                cl.show(cardSet, "LoginView"); //  shows the login card after logout button is clicked
            }
        });


        //ChangeListener in UserpageDisplay that listens for the search button to be clicked to show the search results from the user
        userListPage.addSearchListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                //sets the testDisplay with the information from searching the game
                searchResults.setGameDisplay(userListPage.getSearchResult());
                searchResults.setSearchResultLabel("Search Results for:  " + userListPage.getTestGame().getTitle());
                cl.show(cardSet, "Search Results"); //shows the Search Result card
            }
        });
        //ChangeListener in UserList display that refreshes the panel after the delete button is clicked on a game
        userListPage.deleteGameListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
             userListPage.createTable(m_user);
            }
        });

        //ChangeListener in the UserList page that listens for the logout button to be clicked so that the user can go back to the loginScreen
        userListPage.addlogoutListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                m_user.getGameLists().get(0).clear(); //clears the user's gamelist
                cl.show(cardSet,  "LoginView"); // shows the loginView
            }
        });

        //ChangeListener in searchResults that returns the user  to their page.
        searchResults.addReturntoUserPage(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                userListPage.createTable(m_user); //creates the a new table with the user's added games
                cl.show(cardSet,"UserView"); //shows the user's new list
            }
        });

        //changeListener that refreshes the searchResult panel with the new game that the user searched.
        searchResults.addAnotherGame(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                searchResults.setSearchResultLabel("Search Results for:  " + searchResults.getTestGame().getTitle()); //sets the label for the new game
                searchResults.setGameDisplay(searchResults.getSearchResult()); //refreshes the display
            }
        });

    }

    }



