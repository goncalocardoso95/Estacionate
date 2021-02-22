package com.example.estacionate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentLocal extends Fragment {

    private List<Historico> listHistorico = new ArrayList<Historico>();
    private ArrayAdapter<Historico> arrayAdapterHistorico;
    ListView lv;


    private void eventDatabase() {
        DatabaseReference ref = Clipboard.getFirebaseDatabase().getReference();

        ref.child("Historico").child(Clipboard.getLoggedUserGuid()).orderByChild("local").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listHistorico.clear();
                for (DataSnapshot objDS : dataSnapshot.getChildren()) {
                    Historico h = objDS.getValue(Historico.class);
                    listHistorico.add(h);
                }
                arrayAdapterHistorico = new ArrayAdapter<Historico>(getContext(), android.R.layout.simple_list_item_1, listHistorico);
                lv.setAdapter(arrayAdapterHistorico);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_local, container, false);



        lv = (ListView) v.findViewById(R.id.lPrec);

        eventDatabase();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent  = new Intent(getContext(), HistoricoMaps.class);
                Clipboard.setLatitude(listHistorico.get(position).getLatitude());
                Clipboard.setLongitude(listHistorico.get(position).getLongitude());
                Clipboard.setLocal(listHistorico.get(position).getLocal());
                startActivity(intent);
            }
        });


        return v;
    }

}
