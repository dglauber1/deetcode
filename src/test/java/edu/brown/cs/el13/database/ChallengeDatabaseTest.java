package edu.brown.cs.el13.database;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import edu.brown.cs.deet.database.ChallengeDatabase;

public class ChallengeDatabaseTest {
  @Test
  public void insertChallengeTest() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // adding a new Challenge
      assertTrue(db.insertNewChallenge("add-one", "addone",
          "/challenges/addone", "Integers"));
      List<String> info = db.getChallenge("add-one");

      assertTrue(info.get(0).equals("add-one"));
      assertTrue(info.get(1).equals("addone"));
      assertTrue(info.get(2).equals("/challenges/addone"));
      assertTrue(info.get(3).equals("Integers"));

      db.deleteChallenge("add-one");

      // adding a repeat Challenge
      assertTrue(db.insertNewChallenge("add-one", "addone",
          "/challenges/addone", "Integers"));
      assertTrue(!db.insertNewChallenge("add-one", "addone",
          "/challenges/addone", "Integers"));
      db.deleteChallenge("add-one");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void doesChallengeExistTests() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      assertTrue(db.insertNewChallenge("add-one", "addone",
          "/challenges/addone", "Integers"));
      // this challenge does exist
      assertTrue(db.doesChallengeExist("add-one"));
      // this challenge does not exist
      assertTrue(!db.doesChallengeExist("addtwo"));

      db.deleteChallenge("add-one");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void getChallengeTests() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // adding a new Challenge
      assertTrue(db.insertNewChallenge("sub-Two", "subTwo",
          "/challenges/subTwo", "Integers"));

      // checking the results from getChallenge
      List<String> res = db.getChallenge("sub-Two");
      assertTrue(res.size() == 4);
      assertTrue(res.get(0).equals("sub-Two"));
      assertTrue(res.get(1).equals("subTwo"));
      assertTrue(res.get(2).equals("/challenges/subTwo"));
      assertTrue(res.get(3).equals("Integers"));

      db.deleteChallenge("sub-Two");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void editChallengeTests() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // adding the challenge
      assertTrue(db.insertNewChallenge("sub-Two", "subTwo",
          "/challenges/subTwo", "Integers"));
      List<String> res = db.getChallenge("sub-Two");
      assertTrue(res.size() == 4);
      assertTrue(res.get(0).equals("sub-Two"));
      assertTrue(res.get(1).equals("subTwo"));
      assertTrue(res.get(2).equals("/challenges/subTwo"));
      assertTrue(res.get(3).equals("Integers"));

      // now edit the challenge with a name that doesn't already exist in the
      // database
      assertTrue(db.editChallenge("sub-Two", "sub-One", "subOne",
          "/challenges/subOne", "Integers"));
      res = db.getChallenge("sub-One");
      assertTrue(res.size() == 4);
      assertTrue(res.get(0).equals("sub-One"));
      assertTrue(res.get(1).equals("subOne"));
      assertTrue(res.get(2).equals("/challenges/subOne"));
      assertTrue(res.get(3).equals("Integers"));

      db.deleteChallenge("sub-One");

      // a test for when the new challenge name already exists in the database
      assertTrue(db.insertNewChallenge("sub-One", "subOne",
          "/challenges/subOne", "Integers"));
      assertTrue(db.insertNewChallenge("sub-Three", "subThree",
          "/challenges/subThree", "Integers"));
      assertTrue(!db.editChallenge("subOne", "sub-Three", "subThree",
          "/challenges/subOne", "Integers"));

      db.deleteChallenge("sub-One");
      db.deleteChallenge("sub-Three");
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void deleteChallengeTests() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // adding the challenge
      assertTrue(db.insertNewChallenge("sub-Two", "subTwo",
          "/challenges/subTwo", "Integers"));
      db.deleteChallenge("sub-Two");

      List<String> res = db.getChallenge("sub-Two");

      // simply testing to see if a row for that challenge exists in the table
      assertTrue(res.size() == 0);
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void testsForChallengeTests() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      // inserting tests
      db.insertTestsForChallenge("reverse", "java");
      db.insertTestsForChallenge("reverse", "python");

      List<String> languages = db.getLanguagesSupported("reverse");

      // checking if the tests were inserted correctly and if
      // getLangaugesSupported works
      assertTrue(languages.size() == 3);
      assertTrue(languages.get(0).equals("java"));
      assertTrue(languages.get(1).equals("java"));
      assertTrue(languages.get(2).equals("python"));

      // deletes languages supported and checks if it is done correctly
      db.deleteLanguageSupported("reverse", "java");
      db.deleteLanguageSupported("reverse", "python");

      languages = db.getLanguagesSupported("reverse");

      assertTrue(languages.size() == 0);
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void doesCategoryExistTest() {
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      assertTrue(db.doesCategoryExist("lists"));
      assertTrue(db.doesCategoryExist("integers"));
      assertTrue(!db.doesCategoryExist("hash"));
    } catch (SQLException e) {
      assertTrue(false);
    }
  }

  @Test
  public void getAllCategoriesTests() {
    // there are categories and there are duplicates
    try (ChallengeDatabase db = new ChallengeDatabase(
        "testdata/challengeDatabaseTester.sqlite3")) {
      List<String> categories = db.getAllCategories();
      assertTrue(categories.size() == 3);
      assertTrue(categories.get(0).equals("lists"));
      assertTrue(categories.get(1).equals("integers"));
      assertTrue(categories.get(2).equals(""));
    } catch (SQLException e) {
      assertTrue(false);
    }
  }
}
