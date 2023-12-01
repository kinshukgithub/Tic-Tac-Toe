package androidsamples.java.tictactoe;
import static com.google.firebase.database.FirebaseDatabase.getInstance;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//    }
//}


public class ExampleUnitTest {


    @Mock
    private FirebaseDatabase mockedFirebaseDatabase;

    @Mock
    private DatabaseReference mockedDatabaseReference;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the behavior of FirebaseDatabase.getInstance()
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
        // Mocking any other behavior you need
        when(mockedFirebaseDatabase.getReference("yourReference")).thenReturn(mockedDatabaseReference);
    }


    @Test
    public void testGameModelInitialization() {
        // Arrange
        String host = "player1";
        String gameId = "123";

        // Act
        GameModel gameModel = new GameModel(host, gameId);

        // Assert
        assertNotNull(gameModel);
        assertEquals(host, gameModel.getHost());
        assertTrue(gameModel.getIsOpen());
        assertEquals(gameId, gameModel.getGameId());
        assertEquals(1, gameModel.getTurn());
        assertFalse(gameModel.isForfeited());

        // Assert default game array is initialized
        assertEquals(Arrays.asList("", "", "", "", "", "", "", "", ""), gameModel.getGameArray());
    }

    @Test
    public void testGameModelTurnUpdate() {
        // Arrange
        GameModel gameModel = new GameModel("player1", "123");

        // Act
        gameModel.setTurn(2);

        // Assert
        assertEquals(2, gameModel.getTurn());
    }

    @Test
    public void testGameModelForfeitedUpdate() {
        // Arrange
        GameModel gameModel = new GameModel("player1", "123");

        // Act
        gameModel.setForfeited(true);

        // Assert
        assertTrue(gameModel.isForfeited());
    }
    @Test
    public void test4() {
        GameModel gameModel = new GameModel("player1", "123");

        // Your assertions or other test logic
        if(mockedFirebaseDatabase!=null){
            DatabaseReference  gameReference = mockedFirebaseDatabase.getReference("junit_test_games").child("test");


            gameModel.gameArray.set(0,"x");
            gameModel.gameArray.set(2,"O");
            gameModel.gameArray.set(3,"x");
            gameModel.gameArray.set(5,"O");

            gameModel.gameArray.set(7,"x");
            // SET SOME 3 or 4

            //then
            gameReference.setValue(gameModel);
            verify(gameReference, times(1)).setValue(gameModel);



            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This block will be called when data changes
                    // Retrieve the value from the dataSnapshot
                    GameModel   retrievedGameModel = dataSnapshot.getValue(GameModel.class);
                    assertEquals( gameModel.gameArray.get(0),"X");
                    assertEquals( gameModel.gameArray.get(2),"O");
                    assertEquals( gameModel.gameArray.get(3),"X");
                    assertEquals( gameModel.gameArray.get(5),"O");
                    assertEquals( gameModel.gameArray.get(7),"X");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors if necessary
                }
            });

        }


    }

    @Test
    public void test5() {
        GameModel gameModel = new GameModel("player1", "123");

        // Your assertions or other test logic
        if(mockedFirebaseDatabase!=null){
            DatabaseReference  gameReference = mockedFirebaseDatabase.getReference("junit_test_games").child("test");


            gameModel.gameArray.set(0,"x");
            gameModel.gameArray.set(2,"O");
            gameModel.gameArray.set(4,"x");
            gameModel.gameArray.set(5,"O");
            gameModel.gameArray.set(8,"x");
            // SET SOME 3 or 4

            //then
            gameReference.setValue(gameModel);
            verify(gameReference, times(1)).setValue(gameModel);



            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This block will be called when data changes
                    // Retrieve the value from the dataSnapshot
                    GameModel   retrievedGameModel = dataSnapshot.getValue(GameModel.class);
                    assertEquals( gameModel.gameArray.get(0),"X");
                    assertEquals( gameModel.gameArray.get(2),"O");
                    assertEquals( gameModel.gameArray.get(4),"X");
                    assertEquals( gameModel.gameArray.get(5),"O");
                    assertEquals( gameModel.gameArray.get(8),"X");
//WINNING IS 1
                    assertEquals(gameModel.checkWin(),1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors if necessary
                }
            });

        }


    }
    @Test
    public void test6() {
        GameModel gameModel = new GameModel("player1", "123");

        // Your assertions or other test logic
        if(mockedFirebaseDatabase!=null){
            DatabaseReference  gameReference = mockedFirebaseDatabase.getReference("junit_test_games").child("test");


            gameModel.gameArray.set(0,"x");
            gameModel.gameArray.set(4,"x");
            gameModel.gameArray.set(7,"x");
            // SET SOME 3 or 4

            //then
            gameReference.setValue(gameModel);
            verify(gameReference, times(1)).setValue(gameModel);



            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This block will be called when data changes
                    // Retrieve the value from the dataSnapshot
                    GameModel   retrievedGameModel = dataSnapshot.getValue(GameModel.class);
                    assertEquals( gameModel.gameArray.get(0),"X");
                    assertEquals( gameModel.gameArray.get(4),"X");
                    assertEquals( gameModel.gameArray.get(7),"X");


                    assertEquals(gameModel.checkWin(),-1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors if necessary
                }
            });

        }


    }


    @Test
    public void test7() {
        GameModel gameModel = new GameModel("player1", "123");

        // Your assertions or other test logic
        if(mockedFirebaseDatabase!=null){
            DatabaseReference  gameReference = mockedFirebaseDatabase.getReference("junit_test_games").child("test");


            gameModel.gameArray.set(0,"x");
            gameModel.gameArray.set(4,"O");

            // SET SOME 3 or 4

            //then
            gameReference.setValue(gameModel);
            verify(gameReference, times(1)).setValue(gameModel);



            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This block will be called when data changes
                    // Retrieve the value from the dataSnapshot
                    GameModel   retrievedGameModel = dataSnapshot.getValue(GameModel.class);
                    assertEquals( gameModel.gameArray.get(0),"X");
                    assertEquals( gameModel.gameArray.get(4),"O");


//not in draw state
                    assertEquals(gameModel.checkDraw(),false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors if necessary
                }
            });

        }


    }

}
