package recharge.com.myrechargegallery;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Statement extends Fragment {

    View view;
    PrefManager prefManager;
    ListView listView;
    ArrayList<Item> alItem;

    public static Statement newInstance() {
        Statement fragment = new Statement();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.statement_fragment, container, false);
        DrawerActivity.setTitle("Statement");

        prefManager = new PrefManager(getActivity());

        listView = (ListView) view.findViewById(R.id.lvStatement);

        alItem = new ArrayList<>();

        Item item = new Item();
        item.setOperator("Idea");item.setNumber("8421881175");item.setAmt("499");
        item.setStatus("Success");item.setTransactionId("txt12458963");
        item.setCommission("3.0");item.setSerCharge("-1");item.setBalance("5002");
        item.setDeductAmt("-98");item.setTimestamp("10:15 AM");
        alItem.add(item);

        item = new Item();
        item.setOperator("Idea");item.setNumber("8421881175");item.setAmt("499");
        item.setStatus("Success");item.setTransactionId("txt12458963");
        item.setCommission("3.0");item.setSerCharge("-1");item.setBalance("5002");
        item.setDeductAmt("-98");item.setTimestamp("10:15 AM");
        alItem.add(item);

        listView.setAdapter(new ItemAdapter(getActivity(), alItem));

        return view;
    }

    class Item {
        String operator, number, amt, status, transactionId, commission, serCharge, balance, deductAmt, timestamp;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDeductAmt() {
            return deductAmt;
        }

        public void setDeductAmt(String deductAmt) {
            this.deductAmt = deductAmt;
        };

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setAmt(String amt) {
            this.amt = amt;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public void setSerCharge(String serCharge) {
            this.serCharge = serCharge;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getOperator() {
            return operator;
        }

        public String getNumber() {
            return number;
        }

        public String getAmt() {
            return amt;
        }

        public String getStatus() {
            return status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getCommission() {
            return commission;
        }

        public String getSerCharge() {
            return serCharge;
        }

        public String getBalance() {
            return balance;
        }
    }

    class ItemAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;
        private ArrayList<Item> arrayList;// = new ArrayList<Place>();

        public ItemAdapter(Context context, ArrayList<Item> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder holder;
            final Item group = arrayList.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_statement, null);
                holder.tvOperator = (TextView) convertView.findViewById(R.id.tvLIOperator);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tvLINumber);
                holder.tvAmt = (TextView) convertView.findViewById(R.id.tvLIAmount);
                holder.tvStatus = (TextView) convertView.findViewById(R.id.tvLIStatus);
                holder.tvTransactionId = (TextView) convertView.findViewById(R.id.tvLITrasactionId);
                holder.tvCommission = (TextView) convertView.findViewById(R.id.tvLICommission);
                holder.tvSerCharge = (TextView) convertView.findViewById(R.id.tvLISerCharge);
                holder.tvBalance = (TextView) convertView.findViewById(R.id.tvLIBalance);
                holder.tvDeductAmt = (TextView) convertView.findViewById(R.id.tvLIDeductAmt);
                holder.tvTimestamp = (TextView) convertView.findViewById(R.id.tvLITimestamp);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvOperator.setText(group.getOperator());
            holder.tvNumber.setText(group.getNumber());
            holder.tvAmt.setText(group.getAmt());
            holder.tvStatus.setText(group.getStatus());
            holder.tvTransactionId.setText(group.getTransactionId());
            holder.tvCommission.setText(group.getCommission());
            holder.tvSerCharge.setText(group.getSerCharge());
            holder.tvBalance.setText(group.getBalance());
            holder.tvDeductAmt.setText(group.getDeductAmt());
            holder.tvTimestamp.setText(group.getTimestamp());

            /*Glide.with(getActivity())
                    .load(group.getPath())
                    .error(R.drawable.tree2)
                    .into(holder.imgPath);*/

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvOperator, tvNumber, tvAmt, tvStatus, tvTransactionId, tvCommission, tvSerCharge, tvBalance, tvDeductAmt, tvTimestamp;
    }
}


