<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/optimus_dark">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:background="@color/optimus_blue"
        android:padding="16dp"
        android:gravity="center_vertical">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="HIDE COUNTRIES / CATEGORIES"
            android:textColor="@color/optimus_light"
            android:textSize="18sp"
            android:textStyle="bold" />

    </LinearLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <LinearLayout
            android:id="@+id/checkboxContainer"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp" />

    </ScrollView>

    <Button
        android:id="@+id/saveSettingsButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Save"
        android:textColor="@color/optimus_light"
        android:backgroundTint="@color/optimus_red" />

</LinearLayout>
