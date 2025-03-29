// Generated by view binder compiler. Do not edit!
package com.example.shelfshare.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.shelfshare.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityAddBookBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final Button btnAddBook;

  @NonNull
  public final EditText etBookAuthor;

  @NonNull
  public final EditText etBookLocation;

  @NonNull
  public final EditText etBookTitle;

  private ActivityAddBookBinding(@NonNull ScrollView rootView, @NonNull Button btnAddBook,
      @NonNull EditText etBookAuthor, @NonNull EditText etBookLocation,
      @NonNull EditText etBookTitle) {
    this.rootView = rootView;
    this.btnAddBook = btnAddBook;
    this.etBookAuthor = etBookAuthor;
    this.etBookLocation = etBookLocation;
    this.etBookTitle = etBookTitle;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAddBookBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAddBookBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_add_book, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAddBookBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnAddBook;
      Button btnAddBook = ViewBindings.findChildViewById(rootView, id);
      if (btnAddBook == null) {
        break missingId;
      }

      id = R.id.etBookAuthor;
      EditText etBookAuthor = ViewBindings.findChildViewById(rootView, id);
      if (etBookAuthor == null) {
        break missingId;
      }

      id = R.id.etBookLocation;
      EditText etBookLocation = ViewBindings.findChildViewById(rootView, id);
      if (etBookLocation == null) {
        break missingId;
      }

      id = R.id.etBookTitle;
      EditText etBookTitle = ViewBindings.findChildViewById(rootView, id);
      if (etBookTitle == null) {
        break missingId;
      }

      return new ActivityAddBookBinding((ScrollView) rootView, btnAddBook, etBookAuthor,
          etBookLocation, etBookTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
