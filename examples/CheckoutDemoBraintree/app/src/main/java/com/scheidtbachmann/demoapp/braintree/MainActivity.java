package com.scheidtbachmann.demoapp.braintree;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.scheidtbachmann.demoapp.R;
import com.scheidtbachmann.entervocheckoutplugin.core.SBCheckOut;
import com.scheidtbachmann.entervocheckoutplugin.delegation.AssetType;
import com.scheidtbachmann.entervocheckoutplugin.delegation.Environment;
import com.scheidtbachmann.entervocheckoutplugin.delegation.IdentificationType;
import com.scheidtbachmann.entervocheckoutplugin.delegation.LogLevel;
import com.scheidtbachmann.entervocheckoutplugin.delegation.SBCheckOutDelegate;
import com.scheidtbachmann.entervocheckoutplugin.delegation.SBCheckOutStatus;
import com.scheidtbachmann.entervocheckoutplugin.dto.SBCheckOutTransaction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements SBCheckOutDelegate {

    private SBCheckOut plugin;
    private final String DEMO_TICKET = "869954023633772111";
    private final String API_KEY = "51885707-b4a3-424c-92a8-a03dcbf7f484";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();

                plugin = SBCheckOut.newInstance(API_KEY, Environment.SANDBOX);
                tx.replace( R.id.tobeplaced, plugin, SBCheckOut.PLUGIN_FRAGEMENT_TAG);
                tx.commit();
                plugin.setDelegate( MainActivity.this);

                /*
                 * example for setting style assets:
                 * int sadIconId = getResources().getIdentifier("mysad", "drawable", getApplicationContext().getPackageName());
                 * Drawable pic = getResources().getDrawable( sadIconId, null);
                 * plugin.setAsset( pic, AssetType.IMAGE_FAIL);
                 *
                 * example for overriding language:
                 * plugin.setLanguage("en");
                 *
                 */

                String styles = getCustomStyleSheetAsString("mystyles.css");
                plugin.setAsset( styles, AssetType.STYLESHEET);
                plugin.start ( DEMO_TICKET, IdentificationType.BARCODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMessage(LogLevel level, String message) {

        Log.i( "DEMOAPP", "LOG MESSAGE " + message);
    }

    public void onError( String message) {

        Log.i( "DEMOAPP", "ERROR MESSAGE" + message);
    }

    public void onStatus(SBCheckOutStatus newStatus, final SBCheckOutTransaction info) {

        Log.i( "DEMOAPP", "STATUS CHANGED TO " + newStatus.name());
        if ( newStatus == SBCheckOutStatus.FLOW_FINISHED) {
            Log.i("DEMOAPP", info.toString());
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showReceipt(info);
                }
            });
        }
    }

    private void showReceipt( final SBCheckOutTransaction data) {

        String receiptText =
                "RECEIPT NO. " + data.getUnique_pay_id() + "\n" +
                        "BRAINTREE TXN REF. " + data.getBraintree_transaction_id() + "\n" +
                        "FACILITY " + data.getFacility() + "\n" +
                        "ENTRY TIME " + formattedTime( data.getEntrytime()) + "\n" +
                        "TRANSACTION TIME " + formattedTime(data.getTransactionTime()) + "\n" +
                        "TOTAL AMOUNT " + String.valueOf( data.getAmount()) + "\n" +
                        "INCLUDING " + String.valueOf( data.getVat_amount()) + " VAT (" + data.getVat_rate() + ")\n" +
                        "TICKET NO. " + data.getEpan();

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("YOUR RECEIPT")
                .setMessage( receiptText)
                .setCancelable(false)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
    }

    private String formattedTime( String entervoTime) {

        return
                entervoTime.substring(  6,  8) + "." +
                        entervoTime.substring(  4,  6) + "." +
                        entervoTime.substring(  0,  4) + " " +
                        entervoTime.substring(  8, 10) + ":" +
                        entervoTime.substring( 10, 12) + ":" +
                        entervoTime.substring( 12, 14);
    }


    public void onConductPayment( String nonce) {}

    private String getCustomStyleSheetAsString( String fileName) {

        String stylesAsString = "";
        InputStream is = MainActivity.class.getResourceAsStream( "/assets/" + fileName);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            while (true) {
                try {
                    int character = is.read();
                    if (character != -1) {
                        os.write(character);
                    } else {
                        break;
                    }

                } catch (IOException ioEx) {
                }
            }
            is.close();
            stylesAsString = os.toString();
            os.close();

        } catch(IOException ioEx){
        }
        return stylesAsString;
    }
}
