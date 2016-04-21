package edu.brown.cs.el13.database;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.deet.database.ChallengeDatabase;

public class ChallengeDatabaseTest {
  @Test
  public void insertChallengeTest() {
    ChallengeDatabase db =
        new ChallengeDatabase("testdata/challengeDatabaseTester.sqlite3");

    try {
      // adding a new Challenge
      assertTrue(db.insertNewChallenge("addone", "/challenges/addone",
          "Integers"));
      List<String> info = db.getChallenge("addone");
      assertTrue(info.get(0).equals("addone"));
      assertTrue(info.get(1).equals("/challenges/addone"));
      assertTrue(info.get(2).equals("Integers"));

      db.deleteChallenge("addone");

      // adding a repeat Challenge
      assertTrue(db.insertNewChallenge("addone", "/challenges/addone",
          "Integers"));
      assertTrue(!db.insertNewChallenge("addone", "/challenges/addone",
          "Integers"));
      db.deleteChallenge("addone");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void doesChallengeExistTests() {
    ChallengeDatabase db =
        new ChallengeDatabase("testdata/challengeDatabaseTester.sqlite3");

    try {
      assertTrue(db.insertNewChallenge("addone", "/challenges/addone",
          "Integers"));
      // this challenge does exist
      assertTrue(db.doesChallengeExist("addone"));
      // this challenge does not exist
      assertTrue(!db.doesChallengeExist("addtwo"));

      db.deleteChallenge("addone");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void getChallengeTests() {
    ChallengeDatabase db =
        new ChallengeDatabase("testdata/challengeDatabaseTester.sqlite3");

    try {
      // adding a new Challenge
      assertTrue(db.insertNewChallenge("subTwo", "/challenges/subTwo",
          "Integers"));

      // checking the results from getChallenge
      List<String> res = db.getChallenge("subTwo");
      assertTrue(res.size() == 3);
      assertTrue(res.get(0).equals("subTwo"));
      assertTrue(res.get(1).equals("/challenges/subTwo"));
      assertTrue(res.get(2).equals("Integers"));

      db.deleteChallenge("subTwo");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void editChallengeTests() {
    ChallengeDatabase db =
        new ChallengeDatabase("testdata/challengeDatabaseTester.sqlite3");

    try {
      // adding the challenge
      assertTrue(db.insertNewChallenge("subTwo", "/challenges/subTwo",
          "Integers"));
      List<String> res = db.getChallenge("subTwo");
      assertTrue(res.size() == 3);
      assertTrue(res.get(0).equals("subTwo"));
      assertTrue(res.get(1).equals("/challenges/subTwo"));
      assertTrue(res.get(2).equals("Integers"));

      // now edit the challenge with a name that doesn't already exist in the
      // database
      assertTrue(db.editChallenge("subTwo", "subOne", "/challenges/subOne",
          "Integers"));
      res = db.getChallenge("subOne");
      assertTrue(res.size() == 3);
      assertTrue(res.get(0).equals("subOne"));
      assertTrue(res.get(1).equals("/challenges/subOne"));
      assertTrue(res.get(2).equals("Integers"));

      db.deleteChallenge("subOne");

      // a test for when the new challenge name already exists in the database
      assertTrue(db.insertNewChallenge("subOne", "/challenges/subOne",
          "Integers"));
      assertTrue(db.insertNewChallenge("subThree", "/challenges/subThree",
          "Integers"));
      assertTrue(!db.editChallenge("subOne", "subThree", "/challenges/subOne",
          "Integers"));

      db.deleteChallenge("subOne");
      db.deleteChallenge("subThree");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void deleteChallengeTests() {
    ChallengeDatabase db =
        new ChallengeDatabase("testdata/challengeDatabaseTester.sqlite3");

    try {
      // adding the challenge
      assertTrue(db.insertNewChallenge("subTwo", "/challenges/subTwo",
          "Integers"));
      db.deleteChallenge("subTwo");

      List<String> res = db.getChallenge("subTwo");

      // simply testing to see if a row for that challenge exists in the table
      assertTrue(res.size() == 0);
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void testsForChallengeTests() {
    ChallengeDatabase db =
        new ChallengeDatabase("testdata/challengeDatabaseTester.sqlite3");

    try {
      // inserting tests
      db.insertTestsForChallenge("reverse", "Java");
      db.insertTestsForChallenge("reverse", "Python");

      List<String> languages = db.getLanguagesSupported("reverse");

      // checking if the tests were inserted correctly and if
      // getLangaugesSupported works
      assertTrue(languages.size() == 2);
      assertTrue(languages.get(0).equals("Java"));
      assertTrue(languages.get(1).equals("Python"));

      // deletes languages supported and checks if it is done correctly
      db.deleteLanguageSupported("reverse", "Java");
      db.deleteLanguageSupported("reverse", "Python");

      languages = db.getLanguagesSupported("reverse");
      assertTrue(languages.size() == 0);
    } catch (SQLException e) {
      assertTrue(false);
    }
  }
}
