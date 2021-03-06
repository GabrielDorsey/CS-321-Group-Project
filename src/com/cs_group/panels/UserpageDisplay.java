package com.cs_group.panels;

import com.cs_group.managers.FileManager;
import com.cs_group.objects.Game;
import com.cs_group.objects.GameList;
import com.cs_group.objects.User;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class: UserpageDisplay
 * Description: The panel for displaying the user's list. Creates table populated with com.cs_group.objects.User's game list. Also allows user's to delete games from their list.
 * Uses the Observer pattern
 */
public class UserpageDisplay extends JPanel{

    private JPanel userListPanel;
    private JTable m_gameTable;
    private JComboBox genreCombo;
    private JTextField searchBox;
    private JButton searchButton;
    private JButton Logout;
    private JButton deleteButton;
    private JLabel titleLabel;
    ArrayList<ChangeListener> searchListener = new ArrayList<>();
    ArrayList< ChangeListener> logoutListener = new ArrayList<>();
    ArrayList<ChangeListener> deleteListener  = new ArrayList<>();
    User m_currentUser = new User();
    Game m_testGame = new Game();
    GameList m_searchResult = new GameList();
    FileManager m_fileManager = new FileManager();

    /**
     * Constructor: UserpageDisplay
     * Description: Constructor that creates a table with the user's list displayed. Handles search, logout, and delete buttons/listeners, as well as sorting of the lists.
     *
     * @param user : Passes in a user whose list is displayed in the JTable.
     * @throws IOException
     * @throws ParseException
     */
    public UserpageDisplay(User user) throws IOException, ParseException {

        createTable(user);
        createGenreCombo(user);

        searchButton.addActionListener(new ActionListener() {      //search button listener, displays search results
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_testGame.setTitle(searchBox.getText());
                    if(m_fileManager.isGameInList(m_testGame)){
                        m_searchResult = m_fileManager.gamesSearchResult(m_testGame);

                        ChangeEvent event = new ChangeEvent(this);
                        for (ChangeListener listener : searchListener ) {
                            listener.stateChanged(event);
                        }

                    }
                    else{ JOptionPane.showMessageDialog(null, "GAME NOT FOUND", "ERROR", JOptionPane.ERROR_MESSAGE);}
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        Logout.addActionListener(new ActionListener() {         //logout listener, brings user back to the login screen
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_fileManager.saveUserData(user);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                user.getGameLists().get(0).clear();
                genreCombo.setSelectedIndex(0);
                ChangeEvent event = new ChangeEvent(this);
                for (ChangeListener listener : logoutListener ) {
                    listener.stateChanged(event);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {       //delete button listener, allows user to delete a game from their list by selecting its row
            @Override
            public void actionPerformed(ActionEvent e) {
                Game m_remove = new Game();
                int m_row = m_gameTable.getSelectedRow();
                String m_title = (String) m_gameTable.getValueAt(m_row, 0);
                m_remove.setTitle(m_title);

                user.getGameLists().get(0).deleteGame(m_remove);
                ChangeEvent event = new ChangeEvent(this);
                for (ChangeListener listener : deleteListener ) {
                    listener.stateChanged(event);
                }
            }
        });
    }

    /**
     * Method: getUserListPanel
     * Description: Gets the userList for com.cs_group.managers.ScreenManager
     * @return returns the rootPanel
     */
    public JPanel getUserListPanel() {
        return userListPanel;
    }

    /**
     * Method: createTable
     * Description: Populates JTable with user's gameList information. JTable sorted here.
     * @param user: Passes in a user and accesses the user's gameList.
     */
    public void createTable(User user) {
        // will make it look better later
        String[][] m_data = new String[user.getGameLists().get(0).getLength()][4];

        int m_counter = 0;

        for (Game g : (Iterable<Game>)user.getGameLists().get(0)) {
            if(m_counter == user.getGameLists().get(0).getLength()){
                break;
            }
            //temporarily replacing commas with spaces to test sorting genres
            String genreL = g.getGenre().replace(",", " ");
            m_data[m_counter][0] = g.getTitle();
            m_data[m_counter][1] = genreL; //TEMPORARY
            m_data[m_counter][2] = g.getPublisher();
            m_data[m_counter++][3] = g.getRating();
        }
        //sets the combobox string to basic information
        m_gameTable.setModel( new DefaultTableModel(
                m_data,
                new String[]{"Title", "Genre", "Publisher" , "Rating" }
        ));
      TableColumnModel columns = m_gameTable.getColumnModel();
      columns.getColumn(0).setMinWidth(0);
       m_gameTable.setAutoCreateRowSorter(true); // THIS WILL SORT ALPHABETICALLY AUTOMATICALLY

    }

    /**
     * Method: createGenreCombo
     * Description: Adds listener to genre combobox, which is used to display games that match the selected genre.
     * @param user: Passes in a user and accesses the user's gameList.
     */
    public void createGenreCombo(User user){
        genreCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    String genreSelect = (String)genreCombo.getSelectedItem(); //grab genre selection
                    if(genreSelect.equals("Default")){ //if default selected, loads entire List
                        createTable(user);
                    }
                    else{
                        Game m_testGame = new Game();
                        m_testGame.setGenre(genreSelect);
                        User m_testUser = new User();
                        for (Game g : (Iterable<Game>)user.getGameLists().get(0)) {
                            if(g.compareGenre(m_testGame)){
                                m_testUser.getGameLists().get(0).addGame(g);
                            }
                        }
                        createTable(m_testUser);

                    }
                }
            }
        });


    }


    /**
     * Method: addSearchListener
     * Description: adds the listener to the searchButton to search the gameFile for searched criteria and display it
     * @param listener Pass in a changeListener so that it can be added to the searchListener arrayList
     */
    public void addSearchListener(ChangeListener listener) {
        searchListener.add(listener);
    }



    /**
     * Method: addLogoutListener
     * Description: adds the listener to the logout button, which will take the user back to the login screen
     * @param listener Pass in a changeListener so that it can be added to the logoutListener arrayList
     */
    public void addlogoutListener(ChangeListener listener) {logoutListener.add(listener);}



    /**
     * Method: deleteGameListener
     * Description: adds the listener to the delete button to remove the selected row's game from the gameList
     * @param listener Pass in a changeListener so that it can be added to the deleteListener arrayList
     */
    public void deleteGameListener(ChangeListener listener){ deleteListener.add(listener);}



    /**
     * Method: setUserTitle
     * Description: sets titleLabel to the user's name, so "Name's Game Diary" appears as the title
     * @param Name Pass in the user's name
     */
    public void setUserTitle(String Name){
        titleLabel.setText(Name);
    }
    /**
     * Method: getSearchResult
     * Description: returns a list of games that match the user's search
     * @return Returns a gameList object that is filled with the information of every game that matches the users search
     */
    public GameList getSearchResult(){ return m_searchResult;}

    /**
     * Method: getTestGame
     * Description:  gets the Game object of the title that the user searched so that it can be displayed in searchResultsDisplay.
     * @return
     */
    public Game getTestGame(){
        return m_testGame;
    }
}

