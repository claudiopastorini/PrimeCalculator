package it.scripto.models;

import android.util.Log;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import it.scripto.primecalculator.BuildConfig;

import static java.lang.Math.sqrt;

/**
 * Class that allow to calculate prime numbers.
 * @author pincopallino93
 * @version 1.0
 */
public class PrimeCalculator {

    /**
     * Tag for debug printing
     */
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * Boolean for enable/disable debug printing
     */
    private boolean debug;

    /**
     * Set of primes found
     */
    private Set<Long> primeSet = new LinkedHashSet<>();

    /**
     * Vector of all number, to use in Eratosthene's Sieve
     */
    private Vector<Long> primeVector;
    
    // Default constructor
    public PrimeCalculator() {
        this.debug = BuildConfig.DEBUG;
    }

    /**
     * Constructor that call default and set debug.
     * @param debug true if you want enable debug, false otherwise
     */
    public PrimeCalculator(boolean debug) {
        super();
        this.debug = debug;
    }
    
    /**
     * Check if the first number is lower than second and if both are greater than zero.
     * @param lowerBound the lower number
     * @param upperBound the higher number
     * @return true if the couple is valid false otherwise
     */
    public boolean areCongruent(long lowerBound, long upperBound) {
        return lowerBound < upperBound && lowerBound >= 0 && upperBound >= 0;
    }

    /**
     * Check if the number is prime or not using trial division.
     * First of all it checks if the number is already known as prime,
     * if not it starts to use an "advanced" algorithm of division.
     * @author Andrea Cerra, Claudio Pastorini
     * @param number the number to check
     * @return true if number is prime false otherwise
     */
    public boolean isPrimeByTrialDivision(long number) {
        // If is not a known prime checks it
        if (!this.primeSet.contains(number)) {
            // If number is even (or is 0 or 1) => is not a prime number, then return false
            if ((number % 2 == 0) && (number != 2) || (number == 0) || (number == 1)) {
                return false;
            } else {
                // Calculate square root of the number
                long squareRoundedUp = (long) Math.nextUp(sqrt(number));

                if (debug) Log.i(TAG, String.format("Number: %d - Square: %d\n", number, squareRoundedUp));

                // For all number between 2 and square root of the number
                for (long i = 2; i <= squareRoundedUp; ++i) {

                    if (debug) Log.i(TAG,  String.format("i: %d\n", i));

                    // Check if i is a prime number
                    if (isPrimeByTrialDivision(i)) {

                        if (debug) Log.i(TAG, String.format("Not composite\n%d mod %d = %d\n", number, i, number % i));

                        // Check if i is a divisor or not of the number
                        if (number % i == 0) {
                            return false;
                        }
                    }
                }

                // Save the number and return true
                this.primeSet.add(number);
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * Find all the prime numbers below limit number using the Eratosthenes' Sieve.
     * @param limit the limit number
     * @return the prime's set calculated
     */
    private Set<Long> primeNumbersByEratosthenesSieve(long limit) {
        // Initialise vector
        this.initializePrimeVector(limit);

        for (int i = 0; i < primeVector.size(); ++i) {
            long value = this.primeVector.get(i);

            if (value != 0) {
                this.primeSet.add(value);
                for (int j = 2 * (int) value - 2; j <= primeVector.size() - 1; j = j + (int) value) {
                    this.primeVector.set(j, (long) 0);
                }
            }
        }
        // Clear primeVector
        this.primeVector.clear();

        return this.primeSet;
    }

    /**
     * Initialize a vector of number - 2 elements with all number between
     * 2 and number itself.
     * @param number the max number in the vector
     */
    private void initializePrimeVector(long number) {
        // Create vector
        if (this.primeVector == null) {
            this.primeVector = new Vector<>();
        }
        // Generate all number between 2 and number
        for (int i = 0; i <= number - 2; ++i) {
            this.primeVector.add(i, (long) i + 2);
        }

        if (debug) System.out.println(this.primeVector.toString());
    }
}