/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package com.example.litapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.fbreader.toc.TOCTree;
import org.fbreader.toc.TableOfContents;
import org.fbreader.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TableOfContentsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.toc);

        final Intent intent = getIntent();

        final String title = intent.getStringExtra(String.valueOf(TableOfContentsUtil.Key.book_title));
        if (title != null) {
            setTitle(title);
        }

        final String path = intent.getStringExtra(String.valueOf(TableOfContentsUtil.Key.file_path));
        final TableOfContents toc = TableOfContents.read(path);
        if (toc == null) {
            finish();
            return;
        }

        final ListView listView = this.findViewById(android.R.id.list);
        final TableOfContentsAdapter adapter = new TableOfContentsAdapter(
                (Map<Integer, Integer>) intent.getSerializableExtra(String.valueOf(TableOfContentsUtil.Key.page_map)),
                toc.root
        );

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        final int ref = intent.getIntExtra(String.valueOf(TableOfContentsUtil.Key.reference), -1);
        final TOCTree treeToSelect = ref != -1 ? toc.findTreeByReference(ref) : null;
        adapter.selectItem(listView, treeToSelect);
    }

    private final class TableOfContentsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private final Map<Integer, Integer> pageMap;
        private final TOCTree root;
        private final List<TOCTree> itemsList;
        private final HashSet<TOCTree> openItems = new HashSet<TOCTree>();
        private TOCTree selectedTree;

        TableOfContentsAdapter(Map<Integer, Integer> pageMap, TOCTree root) {
            this.pageMap = pageMap;
            this.root = root;
            this.itemsList = new ArrayList<TOCTree>(root.getSize() - 1);
            for (TOCTree tree : root) {
                if (tree != root) {
                    this.itemsList.add(tree);
                }
            }
            this.openItems.add(root);
        }

        @Override
        public TOCTree getItem(int position) {
            return this.itemsList.get(this.indexByPosition(position + 1, this.root) - 1);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return this.indexByPosition(position + 1, this.root);
        }

        private int getCount(TOCTree tree) {
            int count = 1;
            if (this.isOpen(tree)) {
                for (TOCTree subtree : tree.subtrees()) {
                    count += getCount(subtree);
                }
            }
            return count;
        }

        @Override
        public int getCount() {
            return getCount(this.root) - 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_item, parent, false);
            final TOCTree tree = getItem(position);
            view.setBackgroundColor(tree == this.selectedTree ? 0xff8080ff : 0);
            setIcon(ViewUtil.findView(view, R.id.toc_item_icon), tree);
            ViewUtil.setSubviewText(view, R.id.toc_item_text, tree.Text);

            final Integer pageNo;
            if (this.pageMap != null && tree.Reference != null && tree.Reference != -1) {
                pageNo = this.pageMap.get(tree.Reference);
            } else {
                pageNo = null;
            }
            ViewUtil.setSubviewText(
                    view, R.id.toc_item_pageno, pageNo != null ? String.valueOf(pageNo) : ""
            );

            return view;
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final TOCTree tree = getItem(position);
            if (tree.hasChildren()) {
                expandOrCollapseTree(tree);
            } else {
                openBook(tree);
            }
        }

        private void openTree(TOCTree tree) {
            if (tree == null) {
                return;
            }
            while (!this.openItems.contains(tree)) {
                this.openItems.add(tree);
                tree = tree.Parent;
            }
        }

        private void expandOrCollapseTree(TOCTree tree) {
            if (!tree.hasChildren()) {
                return;
            }
            if (this.isOpen(tree)) {
                this.openItems.remove(tree);
            } else {
                this.openItems.add(tree);
            }
            notifyDataSetChanged();
        }

        private boolean isOpen(TOCTree tree) {
            return this.openItems.contains(tree);
        }

        private void selectItem(ListView listView, TOCTree tree) {
            this.selectedTree = tree;
            if (tree == null) {
                return;
            }
            openTree(tree.Parent);
            int index = 0;
            while (true) {
                TOCTree parent = tree.Parent;
                if (parent == null) {
                    break;
                }
                for (TOCTree sibling : parent.subtrees()) {
                    if (sibling == tree) {
                        break;
                    }
                    index += getCount(sibling);
                }
                tree = parent;
                ++index;
            }
            if (index > 0) {
                listView.setSelection(index - 1);
            }
            listView.invalidateViews();
        }

        private void openBook(TOCTree tree) {
            if (tree.Reference != null && tree.Reference != -1) {
                final Intent intent = new Intent();
                intent.putExtra(String.valueOf(TableOfContentsUtil.Key.reference), tree.Reference);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        private int indexByPosition(int position, TOCTree tree) {
            if (position == 0) {
                return 0;
            }
            --position;
            int index = 1;
            for (TOCTree subtree : tree.subtrees()) {
                int count = getCount(subtree);
                if (count <= position) {
                    position -= count;
                    index += subtree.getSize();
                } else {
                    return index + this.indexByPosition(position, subtree);
                }
            }
            throw new RuntimeException("That's impossible!!!");
        }

        private void setIcon(ImageView imageView, TOCTree tree) {
            final Context context = imageView.getContext();
            if (tree.hasChildren()) {
                imageView.setImageResource(
                        this.isOpen(tree) ? R.drawable.ic_button_minus_small : R.drawable.ic_button_plus_small
                );
            } else {
                imageView.setImageDrawable(null);
            }
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();
            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, dm),
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15 * (tree.Level - 1), dm),
                    0, 0, 0
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            imageView.setLayoutParams(params);
        }
    }
}
