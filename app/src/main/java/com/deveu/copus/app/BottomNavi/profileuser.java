package com.deveu.copus.app.BottomNavi;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deveu.copus.app.Adapter.BookSliderAdapter;
import com.deveu.copus.app.Adapter.ViewHolderMyBooks;
import com.deveu.copus.app.Datas.Books;
import com.deveu.copus.app.Datas.MyBooks;
import com.deveu.copus.app.Datas.slide;
import com.deveu.copus.app.R;
import com.deveu.copus.app.ReadingSection.PDFActivity;
import com.deveu.copus.app.Sign_in.Sign_In;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class profileuser extends Fragment {
    FirebaseDatabase mfirebaseDatabase;
    DatabaseReference mRef;
    FirebaseUser currentuser;
    private RecyclerView rv_user;
    RewardedVideoAd adforbooks;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    TextView textView20,textView24;
    String userid;
    String userpp;
    ImageView imageView3;
    private TextView usernameText;
    private CircleImageView tutorpi_profile;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    ViewPager viewPagerProfile;
    List<slide>slides;
    BookSliderAdapter bookSliderAdapter;
   // InterstitialAd adInter;
    public profileuser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.profile_layout, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentuser= FirebaseAuth.getInstance().getCurrentUser();
        textView24=getView().findViewById(R.id.textView24);
        textView20=getView().findViewById(R.id.textView20);

        viewPagerProfile=getView().findViewById(R.id.viewPagerProfile);
        imageView3=getView().findViewById(R.id.imageView3);
        usernameText=getView().findViewById(R.id.username);
        tutorpi_profile=getView().findViewById(R.id.tutorpi_profile);
        rv_user=getView().findViewById(R.id.rv_user);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //check users

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if( user!=null)
                {

                }
                else{

                }
            }
        };


        mAuth.addAuthStateListener(mAuthListener);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), Sign_In.class));
                getActivity().finish();
            }
        });


        Timer timer= new Timer();
        timer.scheduleAtFixedRate(new sliderTime2(),2000,3000);
        sliderPaperAdapter();
        userDownloads();
        userInfo();
        MobileAds.initialize(getContext(),"ca-app-pub-4177258851116225~1431984404");

        adforbooks=MobileAds.getRewardedVideoAdInstance(getContext());
        adforbooks.loadAd("ca-app-pub-4177258851116225/8604252637",new AdRequest.Builder().build());

    }

    private void userDownloads() {
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mfirebaseDatabase.getReference("Book_Downloads").child(currentuser.getUid());
        Query androidbooks = mRef.orderByChild("bookid");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Long numbofBook=dataSnapshot.getChildrenCount();


             if (numbofBook==0){

              textView20.setText("You don't have any book :/");
                 textView24.setVisibility(View.GONE);

             }else {
                 textView20.setVisibility(View.GONE);
                 textView24.setText(numbofBook.toString());
             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<MyBooks, ViewHolderMyBooks> firebaseRecyclerAdapter2 =
                new FirebaseRecyclerAdapter<MyBooks, ViewHolderMyBooks>(
                        MyBooks.class, R.layout.item_book, ViewHolderMyBooks.class, androidbooks
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolderMyBooks viewHolder, MyBooks Books, int position) {

                        viewHolder.setDetails(getActivity().getApplicationContext(),
                                Books.getBookid(), Books.getBookname(), Books.getBookphoto(),
                                Books.getPdflink()

                        );
                        DatabaseReference rtdb  = FirebaseDatabase.getInstance().getReference("BooksReviews")
                                .child(Books.getBookid());

                        rtdb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    long datacount = dataSnapshot.getChildrenCount();
                                    float sum = 0;
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        Map<String,String> map = (Map<String,String>) snapshot.getValue();
                                        String rating = map.get("rating");

                                        float pValue = Float.parseFloat(rating);
                                        sum+=pValue;

                                        float rtx = sum / (float) datacount;

                                        viewHolder.rtw.setRating(rtx);
                                    }
                                }else{
                                    viewHolder.rtw.setRating(0);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public ViewHolderMyBooks onCreateViewHolder(ViewGroup parent, int viewType) {

                        ViewHolderMyBooks viewHolder = super.onCreateViewHolder(parent, viewType);


                        viewHolder.setOnClickListener(new ViewHolderMyBooks.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {



                                if (adforbooks == null || !adforbooks.isLoaded()){
                                    Toast.makeText(getContext(), "Please, control your internet connection!", Toast.LENGTH_SHORT).show();
                                }else{
                                    adforbooks.show();
                                }




                                adforbooks.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoAdLoaded() {
                                    }

                                    @Override
                                    public void onRewardedVideoAdOpened() {

                                    }

                                    @Override
                                    public void onRewardedVideoStarted() {
                                        Toast.makeText(getContext(), "When the video is over, the book will be opened.", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onRewardedVideoAdClosed() {
                                        adforbooks.loadAd("ca-app-pub-4177258851116225/8604252637",new AdRequest.Builder().build());
                                        Toast.makeText(getContext(), "Respect for our labour :)) ", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onRewarded(RewardItem rewardItem) {
                                        String openpdf = getItem(position).getPdflink();

                                        Intent intent = new Intent(getContext(), PDFActivity.class);
                                        intent.putExtra("link",openpdf);
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void onRewardedVideoAdLeftApplication() {

                                    }

                                    @Override
                                    public void onRewardedVideoAdFailedToLoad(int i) {

                                    }

                                    @Override
                                    public void onRewardedVideoCompleted() {

                                    }
                                });

                       /*         if(adInter.isLoaded()){
                                    adInter.show();
                                }

                                adInter.setAdListener(new AdListener(){
                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int i) {
                                        Toast.makeText(getActivity(), "The ad can't be loaded.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAdOpened() {
                                        Toast.makeText(getContext(), "Respect for our labour :)) ", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onAdLeftApplication() {
                                        super.onAdLeftApplication();
                                    }

                                    @Override
                                    public void onAdClosed() {
                                        adInter.loadAd(new AdRequest.Builder().build());
                                        String openpdf = getItem(position).getPdflink();

                                        Intent intent = new Intent(getContext(), PDFActivity.class);
                                        intent.putExtra("link",openpdf);
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void onAdClicked() {
                                        super.onAdClicked();
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        super.onAdImpression();
                                    }
                                });*/



                            }


                            @Override
                            public void onItemLongClick(View view, int position) {
                                //TODO
                            }
                        });

                        return viewHolder;
                    }
                };
        rv_user.setAdapter(firebaseRecyclerAdapter2);
        rv_user.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
    }


    private void userInfo(){

        String fuserid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(fuserid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){




                    String username = dataSnapshot.child("username").getValue().toString();
                    usernameText.setText(username);


                    userpp = dataSnapshot.child("userimage").getValue().toString();

                    Picasso.get().load(userpp).into(tutorpi_profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    public void onResume() {
        adforbooks.resume(getActivity());
        super.onResume();
    }

    @Override
    public void onPause() {
        adforbooks.pause(getActivity());
        super.onPause();
    }

    @Override
    public void onDestroy() {
        adforbooks.destroy(getActivity());
        super.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }


    private void sliderPaperAdapter() {


        slides=new ArrayList<>();

        DatabaseReference homesliders = FirebaseDatabase.getInstance()
                .getReference("ProfileSlider").child("profile");

        homesliders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String title = snapshot.child("title").getValue().toString();
                        String image = snapshot.child("image").getValue().toString();
                        slides.add(new slide(image,title));
                        BookSliderAdapter adapter=new BookSliderAdapter(getContext(),slides);
                        viewPagerProfile.setAdapter(adapter);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // int id = getResources().getIdentifier(name, "drawable", getContext().getPackageName());
        //Drawable drawable = getResources().getDrawable(id);

       /* firstSlides.add(new slide(R.drawable.unity_slidera,"Be gamer Developer"));
        firstSlides.add(new slide(R.drawable.unity_sliderb,"Be gamer Developer"));
        firstSlides.add(new slide(R.drawable.asfcsa,"Be gamer Developer"));
        firstSlides.add(new slide(R.drawable.html_slider,"Do you want to be Web Developer?"));
        firstSlides.add(new slide(R.drawable.python_sliderb,""));*/


    }
    class sliderTime2 extends TimerTask { //to change pics!

        @Override
        public void run() {
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPagerProfile.getCurrentItem()<slides.size()-1){
                        viewPagerProfile.setCurrentItem(viewPagerProfile.getCurrentItem()+1);
                    }else{
                        viewPagerProfile.setCurrentItem(0);
                    }


                }
            });
        }
    }

}




