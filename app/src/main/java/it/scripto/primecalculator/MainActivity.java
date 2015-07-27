package it.scripto.primecalculator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import it.scripto.models.PrimeCalculator;
import it.scripto.util.BaseActivity;


public class MainActivity extends BaseActivity {

    private PrimeCalculator primeCalculator;

    private ArrayAdapter<String> arrayAdapter;

    private MaterialEditText upperBoundEditText;
    private MaterialEditText lowerBoundEditText;
    private TextView progressTextView;
    private TextView resultTextView;
    private FloatingActionButton queryButton;

    private ProgressBar progressBar;
    private PrimeCalculatorAsync primeCalculatorAsync;
    private AsyncState asyncState = AsyncState.WAITING;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Toolbar and set as ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Set title empty
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
        }

        // Create instance of PrimeCalculator
        primeCalculator = new PrimeCalculator(false);

        // Get upper bound edit text
        upperBoundEditText = (MaterialEditText) findViewById(R.id.upper_bound_edit_text);

        // Get lower bound edit text
        lowerBoundEditText = (MaterialEditText) findViewById(R.id.lower_bound_edit_text);

        // Get progress edit text
        progressTextView = (TextView) findViewById(R.id.progress_text_view);

        // Get result edit text
        resultTextView = (TextView) findViewById(R.id.result_text_view);

        // Get query button
        queryButton = (FloatingActionButton) findViewById(R.id.query_button);

        // Get list view
        ListView listView = (ListView) findViewById(R.id.list_view);

        // Get progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Create and set adapter
        arrayAdapter = new ArrayAdapter<>(this, R.layout.simple_row_layout, android.R.id.text1);
        listView.setAdapter(arrayAdapter);

        // Set empty list view
        LinearLayout emptyLinearLayout = (LinearLayout) findViewById(R.id.empty_linear_layout);
        listView.setEmptyView(emptyLinearLayout);

        // Set icon
        refreshIcon();

        // Set onClickListener
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (asyncState) {
                    case WAITING:
                        // Hide keyboard
                        hideKeyboard();

                        long lowerBound;
                        long upperBound;

                        try {
                            lowerBound = Long.parseLong(lowerBoundEditText.getText().toString());
                            upperBound = Long.parseLong(upperBoundEditText.getText().toString());

                            if (primeCalculator.areCongruent(lowerBound, upperBound)) {
                                primeCalculatorAsync = new PrimeCalculatorAsync();
                                primeCalculatorAsync.execute(lowerBound, upperBound);
                            } else {
                                String message = getString(R.string.not_congruent);
                                Log.e(TAG, message);
                                showSnackBarWithMessage(message);
                            }

                        } catch (NumberFormatException numberFormatException) {
                            String message = numberFormatException.getMessage();
                            Log.e(TAG, message);
                            String userMessage = getString(R.string.invalid_number);
                            showSnackBarWithMessage(userMessage);
                        }
                        break;
                    case WORKING:
                        if (primeCalculatorAsync != null) {
                            primeCalculatorAsync.cancel(true);
                        }
                        asyncState = AsyncState.STOPPED;
                        refreshIcon();
                        break;
                    case STOPPED:
                        // Clear all
                        clearAll();
                        // Set new state
                        asyncState = AsyncState.WAITING;
                        // Change icon
                        refreshIcon();
                        // Renable edit text
                        refreshEditText();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // TODO: unhide menu
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that allow to change icon of the floating action button follow the state of the application
     */
    private void refreshIcon() {
        switch (asyncState) {
            case WAITING:
                queryButton.setIcon(R.drawable.ic_action_search);
                break;
            case WORKING:
                queryButton.setIcon(R.drawable.ic_action_stop);
                break;
            case STOPPED:
                queryButton.setIcon(R.drawable.ic_action_delete);
                break;
            default:
                break;
        }
    }

    /**
     * Hide keyboard
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Method that allow to show a snack bar with a custom message
     * @param message to show
     */
    private void showSnackBarWithMessage(String message) {
        Snackbar.with(getApplicationContext())
                .text(message)
                .show(this);
    }

    /**
     * Method that allow to edit text and adapter in order to start new calculation
     */
    private void clearAll() {
        // Clear edit text
        lowerBoundEditText.setText(null);
        upperBoundEditText.setText(null);

        // Clear adapter
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * Method that allow to enable or disable edit texts follow the state of the application
     */
    private void refreshEditText() {
        switch (asyncState) {
            case WAITING:
                enableEditText(upperBoundEditText);
                enableEditText(lowerBoundEditText);
                break;
            case WORKING:
                disableEditText(upperBoundEditText);
                disableEditText(lowerBoundEditText);
                break;
            case STOPPED:
                disableEditText(upperBoundEditText);
                disableEditText(lowerBoundEditText);
                break;
            default:
                break;
        }
    }

    /**
     * Disable the edit text passed
     * @param editText to disable
     */
    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    /**
     * Enable the edit text passed
     * @param editText to enable
     */
    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    /**
     * Enum of states of application
     */
    public enum AsyncState {
        WAITING,
        WORKING,
        STOPPED
    }

    /**
     * AsyncTask use in order to find primes with trial division
     */
    private class PrimeCalculatorAsync extends AsyncTask<Long, Long, Void> {

        /**
         * Numbers of primes found
         */
        private int numberOfPrimes;

        /**
         * Time in millis at start of AsynkTask
         */
        private long startTimer;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Initialize number of primes
            numberOfPrimes = 0;
            // Get start time
            startTimer = System.currentTimeMillis();
            // Set state
            asyncState = AsyncState.WORKING;
            // Change icon
            refreshIcon();
            // Disable edit texts
            refreshEditText();
        }

        @Override
        protected Void doInBackground(Long... params) {
            // Get max and set to progress bar
            int max = (int)(params[1] - params[0]);
            progressBar.setMax(max);

            for (long i = params[0]; i <= params[1]; i++) {
                // Check if i is prime or not
                boolean isPrime = primeCalculator.isPrimeByTrialDivision(i);
                // Increment counter of progress bar and pass prime (if found)
                publishProgress(i, isPrime ? i : -1);

                // Escape early if cancel() is called
                if (isCancelled()) break;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);

            // Get progress
            int value = values[0].intValue();

            // Set progress to progress bar
            progressBar.setProgress(value);

            // Calculate rate and set progress to progress text view
            int rate = (int)(((float) progressBar.getProgress())/progressBar.getMax() * 100);
            progressTextView.setText(String.format(getString(R.string.progress_text_view), rate, progressBar.getProgress(), progressBar.getMax()));
            // Add new prime
            if (values[1] != -1) {
                arrayAdapter.add(String.valueOf(values[1]));
                arrayAdapter.notifyDataSetChanged();
                // Increment number of primes found
                numberOfPrimes++;
                // Set number of primes found
                resultTextView.setText(String.format(getString(R.string.result_text_view), numberOfPrimes));
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            finish();
        }

        /**
         * Same call in case of thread is cancelled or is finished.
         */
        private void finish() {
            // Get finish time
            long finishTimer = System.currentTimeMillis();
            // Set number of primes found and time to finish
            resultTextView.setText(String.format(getString(R.string.result_text_view_with_time), numberOfPrimes, (finishTimer - startTimer) / 1000.00));
            // Set state
            asyncState = AsyncState.STOPPED;
            // Change icon
            refreshIcon();
        }
    }

}
