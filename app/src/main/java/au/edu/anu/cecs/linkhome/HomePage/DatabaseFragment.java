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
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import au.edu.anu.cecs.linkhome.AVL.AVLTree;
import au.edu.anu.cecs.linkhome.Data;
import au.edu.anu.cecs.linkhome.DataAdapter;
import au.edu.anu.cecs.linkhome.R;
import au.edu.anu.cecs.linkhome.Tokenizer.EqualExp;
import au.edu.anu.cecs.linkhome.Tokenizer.Parser;
import au.edu.anu.cecs.linkhome.Tokenizer.Tokenizer;

public class DatabaseFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference database;
    au.edu.anu.cecs.linkhome.DataAdapter DataAdapter;
    HashMap<String, AVLTree<Data>> hashMapAVL;
    ArrayList<Data> list;
    ArrayList<Integer> listImages;
    DataAdapter.ItemClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_database, container, false);
        recyclerView = root.findViewById(R.id.Database);
        database = FirebaseDatabase.getInstance().getReference("Users");
        hashMapAVL = new HashMap<>();
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
             * @param snapshot of the Data
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Data data = dataSnapshot.getValue(Data.class);
                    //Adds the data to an AVL tree according to its city.
                    assert data != null;
                    if (hashMapAVL.containsKey(data.getCity())) {
                        Objects.requireNonNull(hashMapAVL.get(data.getCity())).insert(data);
                    } else {
                        hashMapAVL.put(data.getCity(), new AVLTree<>(data));
                    }

                    list.add(data);
                    int max = 1000;
                    int min = 0;
                    listImages.add((int) (Math.random() * ((max - min) + 1) + min));
                }

                DataAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        inflater.inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Tokenizer tokenizer = new Tokenizer(query);
                Parser parser = new Parser(tokenizer);
                parser.parseExp();
                ArrayList<Object> list = parser.getFinalList();
                if(list.get(0) instanceof EqualExp && list.get(1) instanceof String){
                    AVLTree<Data> hashTree = hashMapAVL.get( (String) list.get(1));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Sort1) {
            Collections.sort(list, Data::compareTo);
            DataAdapter.notifyDataSetChanged();
        } else if (id == R.id.Sort2) {
            Collections.sort(list, (o1, o2) -> o2.compareTo(o1));
            DataAdapter.notifyDataSetChanged();
        }
        return true;
    }

    public void setOnClickListener() {
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