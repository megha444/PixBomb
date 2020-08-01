package com.meghaagarwal.pixbomb;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mInstaList;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInstaList = (RecyclerView) findViewById(R.id.intsa_list);
        mInstaList.setHasFixedSize(true);
        mInstaList.setLayoutManager(new LinearLayoutManager(this));

        /*Code for Floating action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("PixBomb");

        FirebaseRecyclerOptions<Insta> options =
                new FirebaseRecyclerOptions.Builder<Insta>()
                        .setQuery(query, new SnapshotParser<Insta>() {
                            @NonNull
                            @Override
                            public Insta parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Insta(snapshot.child("title").getValue().toString(),
                                        snapshot.child("desc").getValue().toString(),
                                        snapshot.child("image").getValue().toString());
                            }
                        })
                        .build();

        FirebaseRecyclerAdapter <Insta, InstaViewHolder> FBRA = new FirebaseRecyclerAdapter<Insta, InstaViewHolder>(
options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull InstaViewHolder instaViewHolder,final int i, @NonNull Insta insta) {
                instaViewHolder.setTitle(insta.getTitle());
                instaViewHolder.setDesc(insta.getDesc());

                Log.d("Title", insta.getTitle());
                Log.d("Desc", insta.getDesc());

               instaViewHolder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @NonNull
            @Override
            public InstaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.insta_row, parent, false);

                return new InstaViewHolder(view);
            }
        };
        mInstaList.setAdapter(FBRA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static class InstaViewHolder extends RecyclerView.ViewHolder
    {
        public InstaViewHolder(View itemView)
        {
            super(itemView);
            View mView = itemView;
        }
        public  LinearLayout root = (LinearLayout) itemView.findViewById(R.id.list_root);

        public void setTitle(String title)
        {
            TextView post_title = (TextView) itemView.findViewById(R.id.text_title);
            post_title.setText(title);
        }

        public void setDesc(String desc)
        {
            TextView post_desc = (TextView) itemView.findViewById(R.id.text_desc);
            post_desc.setText(desc);
        }
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

        if(id == R.id.addIcon)
        {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}