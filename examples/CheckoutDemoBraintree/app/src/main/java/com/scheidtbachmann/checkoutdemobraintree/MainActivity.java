package com.scheidtbachmann.checkoutdemobraintree;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.scheidtbachmann.entervocheckoutplugin.core.SBCheckOut;
import com.scheidtbachmann.entervocheckoutplugin.delegation.AssetType;
import com.scheidtbachmann.entervocheckoutplugin.delegation.IdentificationType;
import com.scheidtbachmann.entervocheckoutplugin.delegation.LogLevel;
import com.scheidtbachmann.entervocheckoutplugin.delegation.SBCheckOutDelegate;
import com.scheidtbachmann.entervocheckoutplugin.delegation.SBCheckOutStatus;
import com.scheidtbachmann.entervocheckoutplugin.delegation.SBCheckOutTransaction;

import android.util.Log.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity implements SBCheckOutDelegate {

    private SBCheckOut plugin;

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

                int sadIconId = getResources().getIdentifier("mysad", "drawable", getApplicationContext().getPackageName());
                Drawable pic = getResources().getDrawable( sadIconId, null);
                plugin = SBCheckOut.newInstance("51885707-b4a3-424c-92a8-a03dcbf7f484");
                // plugin.setAsset( pic, AssetType.IMAGE_FAIL);
                // plugin.setLanguage("en");
                tx.replace( R.id.tobeplaced, plugin, SBCheckOut.PLUGIN_FRAGEMENT_TAG);
                tx.commit();
                plugin.setDelegate( MainActivity.this);
                String styles = getCustomStyleSheetAsString("mystyles.css");
                plugin.setAsset( styles, AssetType.STYLESHEET);
                plugin.start ( "869954023633772111", IdentificationType.BARCODE);
/*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
 */
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

    public void onStatus(SBCheckOutStatus newStatus, SBCheckOutTransaction info) {

        Log.i( "DEMOAPP", "STATUS CHANGED TO " + newStatus.name());
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
