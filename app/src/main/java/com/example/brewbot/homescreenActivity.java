package com.example.brewbot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.brewbot.httptasks.AddBeer;
import com.example.brewbot.httptasks.GetUserInfo;

import java.util.ArrayList;

public class homescreenActivity extends AppCompatActivity {
    private String selectedBeer;
    private View spinner;
    private View triangle;
    private ImageButton TAP;
    private TextView pilsText;
    private TextView alcoholFreeText;
    private TextView specialText;
    private TextView selectedOption;
    private int spinnerHeight;
    private LinearLayout barplot;
    private LinearLayout days;
    private LinearLayout beerCount;
    private View statisticCard;
    private TextView statTitle;
    private TextView profileTitle;
    private ImageView beerLine;
    private int beerlineHeight = 0;
    private ImageView settingsButton;
    private Boolean settings;
    private TextView accountSettingsButton;
    private View pageIndicatorStat;
    private View pageIndicatorProf;
    private ViewGroup.LayoutParams tapButtonSize;
    private View tapAnimation;
    private TextView pressHoldHint;
    private TextView userName;
    private int cardWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        settings = false;

        // Bind variables to ID
        userName = findViewById(R.id.userName);
        spinner = findViewById(R.id.spinner);
        triangle = findViewById(R.id.selectorTriangle);
        TAP = findViewById(R.id.imageButton);
        pilsText = findViewById(R.id.beerOption);
        alcoholFreeText = findViewById(R.id.alcoholFreeOption);
        specialText = findViewById(R.id.specialOption);
        selectedOption = findViewById(R.id.selectedOption);
        barplot = findViewById(R.id.barplot);
        days = findViewById(R.id.days);
        statisticCard = findViewById(R.id.statisticCard);
        beerCount = findViewById(R.id.beerCount);
        statTitle = findViewById(R.id.statTitle);
        profileTitle = findViewById(R.id.profileTitle);
        beerLine = findViewById(R.id.beerLine);
        settingsButton = findViewById(R.id.settingsButton);
        accountSettingsButton = findViewById(R.id.accountSetttingsButton);
        pageIndicatorStat = findViewById(R.id.pageIndicatorStat);
        pageIndicatorProf = findViewById(R.id.pageIndicatorProf);
        tapAnimation = findViewById(R.id.tapAnimation);
        pressHoldHint = findViewById(R.id.longPressHint);
        settingsButton.setTranslationZ(11);

        // Show username
        userName.setText(User.getInstance().getUsername());

        // Set selected beer
        selectedBeer = "normal";

        // Create bar plot TODO Replace data with actual data
        createBarPlot(User.getInstance().getPastWeekStats());

        // Set page indicators
        pageIndicatorStat.setBackgroundResource(R.drawable.indicatorenabled);
        pageIndicatorProf.setBackgroundResource(R.drawable.indicatordisabled);

        // Set Listeners
        statisticCard.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (barplot.isEnabled()) {
                    pageIndicatorStat.setBackgroundResource(R.drawable.indicatordisabled);
                    pageIndicatorProf.setBackgroundResource(R.drawable.indicatorenabled);
                    getProfileCard();
                }
            }

            @Override
            public void onSwipeRight() {
                if (!barplot.isEnabled()) {
                    pageIndicatorStat.setBackgroundResource(R.drawable.indicatorenabled);
                    pageIndicatorProf.setBackgroundResource(R.drawable.indicatordisabled);
                    getStatCard();
                }
            }
        });

        triangle.setOnClickListener(view -> {
            if (triangle.getRotation() == 180) {
                getBeerOptions();
            }
        });
        spinner.setOnClickListener(view -> {
            if (triangle.getRotation() == 180) {
                getBeerOptions();
            }
        });
        selectedOption.setOnClickListener(view -> {
            if (triangle.getRotation() == 180) {
                getBeerOptions();
            }
        });
        TAP.setOnClickListener(view -> {
            tapButtonAnimation(200);
            ValueAnimator hintAnimator = ValueAnimator.ofFloat(0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
            hintAnimator.addUpdateListener(valueAnimator ->{
                float val = (Float) hintAnimator.getAnimatedValue();
                pressHoldHint.setAlpha(val);
            });
            hintAnimator.setDuration(2000);
            hintAnimator.start();
        });
        TAP.setOnLongClickListener(view -> {
            tapButtonAnimation(400);
            tapBeer();
            return true;
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!settings) {
                    openSettings();
                } else {
                    closeSettings();
                }
            }

            private void closeSettings() {
                settings = false;
                TAP.setEnabled(true);
                settingsButton.setImageResource(R.drawable.baseline_menu_black_24dp);
                ValueAnimator beerDownAnimator = ValueAnimator.ofInt(beerlineHeight * 2, beerlineHeight);
                beerDownAnimator.addUpdateListener(valueAnimator -> {
                    int val = (Integer) beerDownAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams beerLineParams = beerLine.getLayoutParams();
                    beerLineParams.height = val;
                    beerLine.setLayoutParams(beerLineParams);
                });
                beerDownAnimator.setDuration(500);
                beerDownAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        beerLine.setTranslationZ(-10);
                    }
                });

                accountSettingsButton.setEnabled(false);
                ValueAnimator noShowSettings = ValueAnimator.ofFloat(1.0f, 0.0f);
                noShowSettings.addUpdateListener(valueAnimator -> {
                    float value = (float) noShowSettings.getAnimatedValue();
                    accountSettingsButton.setAlpha(value);
                });
                noShowSettings.setDuration(400);
                AnimatorSet closeSettings = new AnimatorSet();
                closeSettings.playTogether(beerDownAnimator, noShowSettings);
                closeSettings.start();
            }
        });

        beerSelectionListeners();
    }

    private void tapButtonAnimation(int time) {
        tapButtonSize = tapAnimation.getLayoutParams();
        ValueAnimator tapAnimator = ValueAnimator.ofFloat((float) tapButtonSize.height, (float) (tapButtonSize.height*1.1), tapButtonSize.height);
        tapAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                float val = (Float) tapAnimator.getAnimatedValue();
                ViewGroup.LayoutParams newButtonParams = tapAnimation.getLayoutParams();
                newButtonParams.height = (int)val;
                newButtonParams.width = (int)val;
                tapAnimation.setLayoutParams(newButtonParams);
            }
        });
        tapAnimator.setDuration(time);
        tapAnimator.start();
    }

    private void openSettings() {
        if (beerlineHeight == 0){
            beerlineHeight = beerLine.getLayoutParams().height;
        }
        TAP.setEnabled(false);
        settings = true;
        settingsButton.setImageResource(R.drawable.baseline_close_black_24dp);
        beerLine.setTranslationZ(10);
        ValueAnimator beerUpAnimator = ValueAnimator.ofInt(beerlineHeight, beerlineHeight * 2);
        beerUpAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) beerUpAnimator.getAnimatedValue();
            ViewGroup.LayoutParams beerLineParams = beerLine.getLayoutParams();
            beerLineParams.height = val;
            beerLine.setLayoutParams(beerLineParams);
        });
        beerUpAnimator.setDuration(500);

        accountSettingsButton.setEnabled(true);
        accountSettingsButton.setTranslationZ(11);
        ValueAnimator showSettings = ValueAnimator.ofFloat(0.0f, 1.0f);
        showSettings.addUpdateListener(valueAnimator -> {
            float value = (float) showSettings.getAnimatedValue();
            accountSettingsButton.setAlpha(value);
        });
        showSettings.setDuration(600);
        AnimatorSet openSettings = new AnimatorSet();
        openSettings.playTogether(beerUpAnimator, showSettings);
        openSettings.start();
    }


    /**
     * Listeners for the selection of beer type
     */
    private void beerSelectionListeners() {
        pilsText.setOnClickListener(view -> {
            selectedOption.setText(pilsText.getText());
            selectedBeer = "beer";
            closeBeerOptions();
        });
        alcoholFreeText.setOnClickListener(view -> {
            selectedOption.setText(alcoholFreeText.getText());
            selectedBeer = "zero";
            closeBeerOptions();
        });
        specialText.setOnClickListener(view -> {
            selectedOption.setText(specialText.getText());
            selectedBeer = "special";
            closeBeerOptions();
        });
    }

    /**
     * Remove profile card and get StatCard
     */
    private void getStatCard() {
        if (cardWidth == 0){
            cardWidth = statisticCard.getLayoutParams().width;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0.01f);
        animator.addUpdateListener(valueAnimator -> {
            float alpha = (float) animator.getAnimatedValue();
            // Remove objects
            statisticCard.setAlpha(alpha);
            profileTitle.setAlpha(alpha);
            // If you want to gradually reduce the width, you can update layout parameters as well
            ViewGroup.LayoutParams params = statisticCard.getLayoutParams();
            params.width = (int) (cardWidth * alpha);
            statisticCard.setLayoutParams(params);
        });
        animator.setDuration(300);
        // Disable Stat items after animation
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                barplot.setEnabled(true);
                days.setEnabled(true);
                beerCount.setEnabled(true);
                statTitle.setEnabled(true);
            }
        });

        ValueAnimator animator2 = ValueAnimator.ofFloat(0.01f, 1f);
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                profileTitle.setEnabled(true);
                statisticCard.setBackgroundResource(R.drawable.rectangle);
                // TODO initialize profile components
            }
        });
        animator2.addUpdateListener(valueAnimator -> {
            float alpha = (float) animator2.getAnimatedValue();
            statisticCard.setAlpha(alpha);
            barplot.setAlpha(alpha);
            days.setAlpha(alpha);
            beerCount.setAlpha(alpha);
            statTitle.setAlpha(alpha);
            // If you want to gradually reduce the width, you can update layout parameters as well
            ViewGroup.LayoutParams params = statisticCard.getLayoutParams();
            params.width = (int) (cardWidth * alpha);
            statisticCard.setLayoutParams(params);
        });
        animator2.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator, animator2);
        animatorSet.start();
    }

    /**
     * Remove statistic card, and show AI profile card
     */
    private void getProfileCard() {
        if (cardWidth == 0){
            cardWidth = statisticCard.getLayoutParams().width;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0.01f);
        animator.addUpdateListener(valueAnimator -> {
            float alpha = (float) animator.getAnimatedValue();
            // Remove objects
            statisticCard.setAlpha(alpha);
            barplot.setAlpha(alpha);
            days.setAlpha(alpha);
            beerCount.setAlpha(alpha);
            statTitle.setAlpha(alpha);
            // If you want to gradually reduce the width, you can update layout parameters as well
            ViewGroup.LayoutParams params = statisticCard.getLayoutParams();
            params.width = (int) (cardWidth * alpha);
            statisticCard.setLayoutParams(params);
        });
        animator.setDuration(300);
        // Disable Stat items after animation
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                barplot.setEnabled(false);
                days.setEnabled(false);
                beerCount.setEnabled(false);
                statTitle.setEnabled(false);
            }

        });
        animator.addListener(new AnimatorListenerAdapter() {
        });

        ValueAnimator animator2 = ValueAnimator.ofFloat(0.01f, 1f);
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                profileTitle.setEnabled(true);
                statisticCard.setBackgroundResource(R.drawable.rectangleblue);
                // TODO initialize profile components
            }
        });
        animator2.addUpdateListener(valueAnimator -> {
            float alpha = (float) animator2.getAnimatedValue();
            statisticCard.setAlpha(alpha);
            profileTitle.setAlpha(alpha);
            // If you want to gradually reduce the width, you can update layout parameters as well
            ViewGroup.LayoutParams params = statisticCard.getLayoutParams();
            params.width = (int) (cardWidth * alpha);
            statisticCard.setLayoutParams(params);
        });
        animator2.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator, animator2);
        animatorSet.start();
    }

    /**
     * Creates a bar plot with the past 7 days
     * Colors Green Blue Red in order of number of beers
     *
     * @param data Number of beers of past 7 days
     */
    private void createBarPlot(ArrayList<int[]> data) {
        String[] daysOfWeek = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};

        for (int i = 0; i < data.size(); i++) {
            View child = barplot.getChildAt(6-i);
            child.setPadding(5, 0, 5, 0);
            int[] beerDay = data.get(6-i);

            // Get heights of bar
            float height = 550.0f / 6 * beerDay[1];
            if (beerDay[1] >= 6) {
                height = 550;
            }

            // Create new LayoutParams for the child and set its width
            LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(0, (int) height, 1);
            child.setLayoutParams(childParams);

            // Set color
            child.setBackgroundResource(R.drawable.bar);
            if (beerDay[1] < 4) {
                child.setBackgroundResource(R.drawable.bluebar);
            }
            if (beerDay[1] < 2) {
                child.setBackgroundResource(R.drawable.greenbar);
            }
            // Add days
            TextView day = new TextView(this);
            day.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            day.setGravity(Gravity.CENTER); // Center the text horizontally
            String text = daysOfWeek[i] + '\n' + beerDay[0];
            day.setText(text);
            day.setTextColor(getResources().getColor(R.color.BrewBotBrown));
            // Add the TextView to the 'days' layout
            days.addView(day);

            // Add beer count
            TextView count = new TextView(this);
            count.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            count.setGravity(Gravity.CENTER);
            String number = String.valueOf(beerDay[1]);
            count.setTextColor(getResources().getColor(R.color.BrewBotBrown));
            if (beerDay[1] < 2) {
                number += "✔";
                count.setTextColor(getResources().getColor(R.color.BrewBotOrange));
            }
            count.setText(number);
            beerCount.addView(count);
        }
    }

    /**
     * Connects to beer tap to physically tap beer
     */
    public void tapBeer() {
        if (beerlineHeight == 0){
            beerlineHeight = beerLine.getLayoutParams().height;
        }
        //TODO implement actual tapping
        AddBeer addbeer = new AddBeer(selectedBeer.toString(), this);
        addbeer.execute();
        ValueAnimator beerUpAnimator = ValueAnimator.ofInt(beerlineHeight, beerlineHeight * 2);
        beerUpAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) beerUpAnimator.getAnimatedValue();
            ViewGroup.LayoutParams beerLineParams = beerLine.getLayoutParams();
            beerLineParams.height = val;
            beerLine.setLayoutParams(beerLineParams);
        });
        beerUpAnimator.setDuration(1500);
        beerUpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                settingsButton.setEnabled(false);
            }
        });

        ValueAnimator beerDownAnimator = ValueAnimator.ofInt(beerlineHeight * 2, beerlineHeight);
        beerDownAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) beerDownAnimator.getAnimatedValue();
            ViewGroup.LayoutParams beerLineParams = beerLine.getLayoutParams();
            beerLineParams.height = val;
            beerLine.setLayoutParams(beerLineParams);
        });
        beerDownAnimator.setDuration(3000);
        beerDownAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                settingsButton.setEnabled(true);
            }
        });

        AnimatorSet beerAnimator = new AnimatorSet();
        beerAnimator.playSequentially(beerUpAnimator, beerDownAnimator);
        beerAnimator.start();
    }

    /**
     * Open the beer selection menu
     */
    public void getBeerOptions() {
        spinnerHeight = spinner.getMeasuredHeight();
        ValueAnimator heightAnimation = ValueAnimator.ofInt(spinnerHeight, (int) (TAP.getMeasuredHeight() * 1.5));
        heightAnimation.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = spinner.getLayoutParams();
            layoutParams.height = val;
            spinner.setLayoutParams(layoutParams);
        });
        ValueAnimator triangleAnimation = ValueAnimator.ofInt(180, 0);
        triangleAnimation.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            triangle.setRotation(val);
        });
        ValueAnimator optionsAnimator = ValueAnimator.ofInt(0, 100);
        optionsAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            float alpha = (float) val / 100;
            alcoholFreeText.setEnabled(true);
            specialText.setEnabled(true);
            pilsText.setEnabled(true);
            alcoholFreeText.setAlpha(alpha);
            specialText.setAlpha(alpha);
            pilsText.setAlpha(alpha);
        });
        ValueAnimator selectedAnimator = ValueAnimator.ofInt(100, 0);
        selectedAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            float alpha = (float) val / 100;
            selectedOption.setAlpha(alpha);
            selectedOption.setEnabled(true);
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(heightAnimation, triangleAnimation, optionsAnimator, selectedAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    /**
     * Close the beer selection menu
     */
    public void closeBeerOptions() {
        ValueAnimator heightAnimation = ValueAnimator.ofInt((int) (TAP.getMeasuredHeight() * 1.5), spinnerHeight);
        heightAnimation.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = spinner.getLayoutParams();
            layoutParams.height = val;
            spinner.setLayoutParams(layoutParams);
        });
        ValueAnimator triangleAnimation = ValueAnimator.ofInt(0, 180);
        triangleAnimation.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            triangle.setRotation(val);
        });
        ValueAnimator optionsAnimator = ValueAnimator.ofInt(100, 0);
        optionsAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            float alpha = (float) val / 100;
            alcoholFreeText.setAlpha(alpha);
            specialText.setAlpha(alpha);
            pilsText.setAlpha(alpha);
            alcoholFreeText.setEnabled(false);
            specialText.setEnabled(false);
            pilsText.setEnabled(false);
        });
        ValueAnimator selectedAnimator = ValueAnimator.ofInt(0, 100);
        selectedAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            float alpha = (float) val / 100;
            selectedOption.setEnabled(true);
            selectedOption.setAlpha(alpha);
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(heightAnimation, triangleAnimation, optionsAnimator, selectedAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

}