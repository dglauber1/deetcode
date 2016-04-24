package edu.brown.cs.deet.execution;

/**
 * Basic Triple class, which allows for a three objects to be passed in a Triple
 * object.
 * @author dglauber
 * @param <S1>
 *          type of first object.
 * @param <S2>
 *          type of second object.
 * @param <S3>
 *          type of third object.
 */
public class Triple<S1, S2, S3> {

  private S1 s1;
  private S2 s2;
  private S3 s3;

  /**
   * Constructor for a Triple object.
   * @param s1
   *          first object.
   * @param s2
   *          second object.
   * @param s3
   *          third object.
   */
  public Triple(S1 s1, S2 s2, S3 s3) {
    this.s1 = s1;
    this.s2 = s2;
    this.s3 = s3;
  }

  /**
   * Getter for first object.
   * @return first object.
   */
  public S1 getFirst() {
    return s1;
  }

  /**
   * Getter for second object.
   * @return second object.
   */
  public S2 getSecond() {
    return s2;
  }

  /**
   * Getter for third object.
   * @return
   */
  public S3 getThird() {
    return s3;
  }
}
