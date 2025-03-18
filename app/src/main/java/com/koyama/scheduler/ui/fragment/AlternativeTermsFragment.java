package com.koyama.scheduler.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.koyama.scheduler.R;
import com.koyama.scheduler.adapter.AlternativeTermAdapter;
import com.koyama.scheduler.model.AlternativeTerm;
import java.util.ArrayList;
import java.util.List;

public class AlternativeTermsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlternativeTermAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alternative_terms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        List<AlternativeTerm> terms = getAlternativeTerms();
        adapter = new AlternativeTermAdapter(terms);
        recyclerView.setAdapter(adapter);

        // Set up terms tabs
        TabLayout termsTabLayout = view.findViewById(R.id.termsTabLayout);
        termsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection (Text Book or Alternative)
                // You can implement filtering or sorting based on the selected term type
                if (tab.getPosition() == 0) {
                    // Text Book selected
                    // Currently displaying both terms by default
                } else {
                    // Alternative selected
                    // If you want to implement filtering by term type, do it here
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed for this implementation
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed for this implementation
            }
        });
    }

    private List<AlternativeTerm> getAlternativeTerms() {
        List<AlternativeTerm> terms = new ArrayList<>();
        
        // Add terms with traffic signs from the image
        terms.add(new AlternativeTerm("No overtaking on the right hand side of the road", "No swerving to the right for overtaking", R.drawable.sign_no_overtaking));
        terms.add(new AlternativeTerm("Motor vehicles only", "Automobiles only", R.drawable.sign_motor_vehicles));
        terms.add(new AlternativeTerm("Square right turn", "Two step right turn", R.drawable.sign_square_right_turn));
        terms.add(new AlternativeTerm("Round right trun", "Direct right turn", R.drawable.sign_round_right_turn));
        terms.add(new AlternativeTerm("Sound horn zone", "Honking zone", R.drawable.sign_sound_horn));
        terms.add(new AlternativeTerm("Slowing down", "Drive slow", R.drawable.sign_slowing_down));
        terms.add(new AlternativeTerm("Pedestrian crossing", "Crosswalk", R.drawable.sign_pedestrian_crossing));
        
        // Add remaining terms from the original image
        terms.add(new AlternativeTerm("hearing impairment sign", "aurally challenged driver's mark"));
        terms.add(new AlternativeTerm("bicycle crossing zone", "bicycle crossing lane"));
        terms.add(new AlternativeTerm("signal", "blinker/winker"));
        terms.add(new AlternativeTerm("load", "cargo/luggage"));
        terms.add(new AlternativeTerm("class 1 driver's license", "category 1 license"));
        terms.add(new AlternativeTerm("class 2 driver's license", "category 2 license"));
        terms.add(new AlternativeTerm("helmet", "crash helmet"));
        terms.add(new AlternativeTerm("blind spot", "dead angle/dead area"));
        terms.add(new AlternativeTerm("indication sign", "designation sign"));
        terms.add(new AlternativeTerm("switch to low beams", "dip high beams"));
        terms.add(new AlternativeTerm("signal", "direction indicator"));
        terms.add(new AlternativeTerm("riding with a passenger (motorcycle)", "double riding"));
        terms.add(new AlternativeTerm("double overtaking", "dual overtaking"));
        terms.add(new AlternativeTerm("use the engine brake", "employ the braking power of the engine"));
        terms.add(new AlternativeTerm("light signal", "flashlight signal"));
        terms.add(new AlternativeTerm("(hand signal by a police officer)", "(hand signal by a police officer)"));
        terms.add(new AlternativeTerm("reaction distance", "free running distance"));
        terms.add(new AlternativeTerm("stop", "halt"));
        terms.add(new AlternativeTerm("special heavy equipment", "heavy-duty special vehicle"));
        terms.add(new AlternativeTerm("obstruct", "interfere"));
        terms.add(new AlternativeTerm("close securely", "latch"));
        terms.add(new AlternativeTerm("special light equipment", "light-duty special vehicle"));
        terms.add(new AlternativeTerm("special light equipment", "special-structure vehicle"));
        terms.add(new AlternativeTerm("light vehicle", "lightweight vehicle"));
        terms.add(new AlternativeTerm("motorway", "limited highway"));
        terms.add(new AlternativeTerm("must not", "may not"));
        terms.add(new AlternativeTerm("middle (size) motor vehicle", "medium vehicle"));
        terms.add(new AlternativeTerm("reduce", "mitigate"));
        terms.add(new AlternativeTerm("beginner driver", "novice driver"));
        terms.add(new AlternativeTerm("general road", "ordinary road"));
        terms.add(new AlternativeTerm("seating capacity", "passenger capacity"));
        terms.add(new AlternativeTerm("regular inspection", "periodic inspection"));
        terms.add(new AlternativeTerm("cross road", "perpendicular traffic"));
        terms.add(new AlternativeTerm("disabled driver's sign", "physically challenged driver's mark"));
        terms.add(new AlternativeTerm("inspection by a driver/routine check/daily inspection", "pre-driving check"));
        terms.add(new AlternativeTerm("railway crossing", "railroad crossing"));
        terms.add(new AlternativeTerm("guide dog", "seeing eye dog"));
        terms.add(new AlternativeTerm("hard shoulder", "shoulder of a road"));
        terms.add(new AlternativeTerm("position lamp", "side marker lamp"));
        terms.add(new AlternativeTerm("press the brake strongly", "slam on the brake"));
        terms.add(new AlternativeTerm("specific middle size vehicle", "specified medium vehicle"));
        terms.add(new AlternativeTerm("stop immediately", "stop readily"));
        terms.add(new AlternativeTerm("straighten your back", "straighten your spine"));
        terms.add(new AlternativeTerm("change lane", "swerve"));
        terms.add(new AlternativeTerm("automobile liability insurance certificate", "third-party liability insurance policy"));
        terms.add(new AlternativeTerm("streetcar", "tram"));
        terms.add(new AlternativeTerm("emergency reflector/warning triangle", "trianglar reflector"));
        terms.add(new AlternativeTerm("wheelbase differential", "turning radius difference/innner wheel difference"));
        terms.add(new AlternativeTerm("around sunset", "twilight"));
        terms.add(new AlternativeTerm("vehicle lane", "vehicular lane"));
        terms.add(new AlternativeTerm("opposite", "vice versa"));
        
        return terms;
    }
} 