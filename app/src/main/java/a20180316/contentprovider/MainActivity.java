package a20180316.contentprovider;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity111";
    Button search;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取系统界面中查找、添加两个按钮
        search = (Button) findViewById(R.id.search);
        add = (Button) findViewById(R.id.add);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 定义两个List来封装系统的联系人信息、指定联系人的电话号码、Email等详情
                final ArrayList<String> names = new ArrayList<String>();
                final ArrayList<ArrayList<String>> details = new ArrayList<ArrayList<String>>();

                Cursor cursor = getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                // 遍历查询结果，获取系统中所有联系人
                while (cursor.moveToNext())
                {
                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract
                                    .Contacts._ID));
                    String name = cursor.getString(cursor
                            .getColumnIndex(ContactsContract
                                    .Contacts.DISPLAY_NAME));
                    Log.d(TAG,"contactId:"+contactId+",name:"+name);
                    names.add(name);

                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId,
                            null,
                            null);

                    ArrayList<String> detail = new ArrayList<>();
                    while (phones.moveToNext())
                    {
                        String phoneNumber = phones.getString(phones
                                        .getColumnIndex(ContactsContract
                                        .CommonDataKinds
                                        .Phone.NUMBER));
                        detail.add("电话号码："+phoneNumber);
                    }
                    phones.close();

                    Cursor emails = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + "="+contactId,
                            null,
                            null
                    );
                    while (emails.moveToNext())
                    {
                        String emailAddress = emails.getString(emails
                                        .getColumnIndex(ContactsContract
                                        .CommonDataKinds
                                        .Email.DATA));
                        detail.add("邮件地址:"+emailAddress);
                    }
                    emails.close();
                    Log.d(TAG,"detail:"+detail);
                    details.add(detail);

                }
                for(int i=0;i<details.size();i++)
                    Log.d(TAG,"details"+"["+i+"]"+details.get(i));
                cursor.close();
                View resultDialog = getLayoutInflater().inflate(R.layout.result,null);
                ExpandableListView listView = resultDialog.findViewById(R.id.list);
                ExpandableListAdapter adapter = new ExpandableListAdapter() {
                    private TextView getTextView()
                    {
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 64);
                        TextView textView = new TextView(MainActivity.this);
                        textView.setLayoutParams(lp);
                        textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                        textView.setPadding(36,0,0,0);
                        textView.setTextSize(20);
                        return textView;
                    }
                    @Override
                    public void registerDataSetObserver(DataSetObserver observer) {

                    }

                    @Override
                    public void unregisterDataSetObserver(DataSetObserver observer) {

                    }

                    @Override
                    public int getGroupCount() {
                        return names.size();
                    }

                    @Override
                    public int getChildrenCount(int groupPosition) {
                        return details.get(groupPosition).size();
                    }

                    @Override
                    public Object getGroup(int groupPosition) {
                        return names.get(groupPosition);
                    }

                    @Override
                    public Object getChild(int groupPosition, int childPosition) {
                        return details.get(groupPosition).get(childPosition);
                    }

                    @Override
                    public long getGroupId(int groupPosition) {
                        return groupPosition;
                    }

                    @Override
                    public long getChildId(int groupPosition, int childPosition) {
                        return childPosition;
                    }

                    @Override
                    public boolean hasStableIds() {
                        return true;
                    }

                    @Override
                    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//                        TextView textView;
//                        if(convertView==null) {
//                            textView = getTextView();
//                            textView.setText(getGroup(groupPosition).toString());
//                            textView.setTag(textView);
//                        }else{
//                            textView = (TextView) convertView.getTag();
//                        }
                        TextView textView = getTextView();
                        textView.setText(getGroup(groupPosition).toString());
                        return textView;
                    }

                    @Override
                    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//                        TextView textView;
//                        if(convertView==null) {
//                            textView = getTextView();
//                            textView.setText(getChild(groupPosition, childPosition).toString());
//                            textView.setTag(textView);
//                        }else {
//                            textView = (TextView) convertView.getTag();
//                        }
//                        Log.d(TAG,"textView:"+textView.getText());
                        TextView textView = getTextView();
                        textView.setText(getChild(groupPosition, childPosition).toString());
                        return textView;
                    }

                    @Override
                    public boolean isChildSelectable(int groupPosition, int childPosition) {
                        return true;
                    }

                    @Override
                    public boolean areAllItemsEnabled() {
                        return false;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public void onGroupExpanded(int groupPosition) {

                    }

                    @Override
                    public void onGroupCollapsed(int groupPosition) {

                    }

                    @Override
                    public long getCombinedChildId(long groupId, long childId) {
                        return 0;
                    }

                    @Override
                    public long getCombinedGroupId(long groupId) {
                        return 0;
                    }
                };
                listView.setAdapter(adapter);

                new AlertDialog.Builder(MainActivity.this)
                        .setView(resultDialog)
                        .setPositiveButton("确定",null)
                        .show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)findViewById(R.id.name))
                        .getText().toString();
                String phone = ((EditText)findViewById(R.id.phone))
                        .getText().toString();
                String email = ((EditText)findViewById(R.id.email))
                        .getText().toString();

                ContentValues values = new ContentValues();
                Uri rawContactUri = getContentResolver()
                        .insert(ContactsContract.RawContacts.CONTENT_URI,values);
                long rawContactId = ContentUris.parseId(rawContactUri);
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID,rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                // 设置联系人名字
                values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
                // 向联系人URI添加联系人名字
                getContentResolver().insert(
                        android.provider.ContactsContract.Data.CONTENT_URI, values);
                values.clear();

                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                // 设置联系人的电话号码
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
                // 设置电话类型
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                // 向联系人电话号码URI添加电话号码
                getContentResolver().insert(
                        android.provider.ContactsContract.Data.CONTENT_URI, values);
                values.clear();

                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                // 设置联系人的Email地址
                values.put(ContactsContract.CommonDataKinds.Email.DATA, email);
                // 设置该电子邮件的类型
                values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
                // 向联系人Email URI添加Email数据
                getContentResolver().insert(
                        android.provider.ContactsContract.Data.CONTENT_URI, values);
                Toast.makeText(MainActivity.this
                        , "联系人数据添加成功" , Toast.LENGTH_SHORT)
                        .show();


            }
        });
    }
}
