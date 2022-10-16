package au.edu.anu.cecs.linkhome.HomePage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import au.edu.anu.cecs.linkhome.AVL.AVLTree;
import au.edu.anu.cecs.linkhome.Data;
import au.edu.anu.cecs.linkhome.DataAdapter;
import au.edu.anu.cecs.linkhome.R;

public class DatabaseFragment extends Fragment  {
    RecyclerView recyclerView;
    DatabaseReference database;
    au.edu.anu.cecs.linkhome.DataAdapter DataAdapter;
    HashMap<String, AVLTree<Data>> hashMapAVL;
    ArrayList<Data> list;
    ArrayList<Integer> listImages;
    DataAdapter.ItemClickListener listener;
    CheckBox cbHeart;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_database, container, false);
        recyclerView = root.findViewById(R.id.Database);
        cbHeart = (CheckBox) root.findViewById(R.id.cbHeart);
        database = FirebaseDatabase.getInstance().getReference("Users");
        hashMapAVL = new HashMap<String, AVLTree<Data>>();
        list = new ArrayList<>();
        listImages = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setOnClickListener();

        DataAdapter = new DataAdapter(getContext(), list, listImages, listener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(DataAdapter);

        setHasOptionsMenu(true);

        database.addValueEventListener(new ValueEventListener() {
            /**
             * Read data from the JSON file in firebase and create new objects of type Data
             * @param snapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Data data = dataSnapshot.getValue(Data.class);
                    //Adds the data to an AVL tree according to its city.
                    assert data != null;
                    if(hashMapAVL.containsKey(data.getCity())){
                        Objects.requireNonNull(hashMapAVL.get(data.getCity())).insert(data);
                    } else {
                        hashMapAVL.put(data.getCity(), new AVLTree<>(data));
                    }

                    list.add(data);
                    int max = 1000;
                    int min = 0;
                    listImages.add((int)(Math.random()*((max-min)+1)+min));
                }
                System.out.println("LIST: " + list);
                System.out.println("HASH: " + hashMapAVL.keySet());

//                Collections.sort(list, new Comparator<Data>() {
//                    @Override
//                    public int compare(Data o1, Data o2) {
//                        return o1.getRent().compareTo(o2.getRent());
//                    }
//                });
                DataAdapter.notifyDataSetChanged();
                checkBox();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        return root;
    }


    public void checkBox(){
        cbHeart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==R.id.Sort1){
            Collections.sort(list, new Comparator<Data>() {
                @Override
                public int compare(Data o1, Data o2) {
                    return o1.getRent().compareTo(o2.getRent());
                }
            });
            DataAdapter.notifyDataSetChanged();
        } else if (id==R.id.Sort2){
            Collections.sort(list, new Comparator<Data>() {
                @Override
                public int compare(Data o1, Data o2) {
                    return o2.getRent().compareTo(o1.getRent());
                }
            });
            DataAdapter.notifyDataSetChanged();
        }
        return true;
    }


    public void setOnClickListener(){
        listener = (v, position) -> {
            Intent intent = new Intent(getContext(), DetailedPage.class);
            intent.putExtra("city", list.get(position).getCity());
            intent.putExtra("address", list.get(position).getAddress());
            intent.putExtra("postal", list.get(position).getPostalZip());
            intent.putExtra("rent", list.get(position).getRent());
            intent.putExtra("image", list.get(position).getImage());
            startActivity(intent);
        };
    }

}