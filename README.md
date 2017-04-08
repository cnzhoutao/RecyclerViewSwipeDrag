本文将带你一步一步的实现recyclerview的拖动排序与侧滑删除
效果图如下：

![有图有真相.gif](http://upload-images.jianshu.io/upload_images/3252031-2c6f25463743b62d.gif?imageMogr2/auto-orient/strip)
接下来就开始动手啦！
先加入包吧
```
   compile 'com.android.support:recyclerview-v7:25.3.0'
   compile 'com.android.support:cardview-v7:25.3.0'
```
在activity_main.xml文件中只有一个RecyclerView
```
  <android.support.v7.widget.RecyclerView
       android:id="@+id/rv"
       android:layout_width="match_parent"
       android:layout_height="match_parent">
   </android.support.v7.widget.RecyclerView>
```
子布局item_rv.xml只有一个TextView
```
 <android.support.v7.widget.CardView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="5dp"
        android:minHeight="40dp"
        android:id="@+id/card"
        app:cardCornerRadius="5dp"
        >
        <TextView
            android:id="@+id/title"
            android:layout_gravity="center"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="23sp"
            android:text="title"/>
    </android.support.v7.widget.CardView>
```
接下来为Recyclerview定义一个Adapter并且继承自RecyclerView.Adapter
```
  
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {
    private List<String> mData;
    public List<String> getDataList(){
        return mData;
    }
    public RvAdapter(List<String> mData) {
        this.mData
                = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_rv, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ViewHolder mHolder = holder;
        mHolder.title.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public ViewHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.title);
        }
    }

    public void onItemDissmiss(int position) {
        //移除数据
        mData.remove(position);
        notifyItemRemoved(position);
    }

}
```
在MainActivity里面找到RecyclerView并且设置它的layoutManager和Adapter，这样就完成了一个列表
```
        rv= (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        RvAdapter adapter=new RvAdapter(mData);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
```
接下来才是真正实现拖动和侧滑删除的时候呢。用的SDK提供的ItemTouchHelper这个工具类，它需要一个CallBack回调函数，这里我们使用ItemTouchHelper.Callback由于ItemTouchHelper.Callback是一个抽象函数，所以需要新建一个类去继承它。
```

public class DragItem extends ItemTouchHelper.Callback {
    private RvAdapter adapter;
    public DragItem(RvAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 返回滑动的方向
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag;
        int swipeFlag;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            //允许上下左右的拖动
            dragFlag = ItemTouchHelper.DOWN
                    | ItemTouchHelper.UP
                    | ItemTouchHelper.RIGHT
                    | ItemTouchHelper.LEFT;

            swipeFlag=0;
        }else{
            dragFlag=ItemTouchHelper.DOWN|ItemTouchHelper.UP;
            swipeFlag=ItemTouchHelper.LEFT;//只允许从右到左的侧滑
        }
        return makeMovementFlags(dragFlag,swipeFlag);
    }

    /**
     * 当用户拖动一个item从旧的位置移动到新的位置时会调用此方法
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adapter.getDataList(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adapter.getDataList(), i, i - 1);
            }
        }
        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * 当用户左右滑动item达到删除条件时会调用此方法
     * 一般达到item的一般宽度时才会删除，否则弹回原位置
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.END) {
            adapter.getDataList().remove(position);
            adapter.notifyItemRemoved(position);
        }
        adapter.onItemDissmiss(position);
    }

    /**
     * 当某个item由静止状态变为滑动或拖动状态时调用此方法
     * @param viewHolder
     * @param actionState
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
            viewHolder.itemView.setBackgroundColor(Color.GRAY);
        }
    }

    /**
     * 当用户操作完某个item动画结束时调用此方法
     * @param recyclerView
     * @param viewHolder
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(0);
    }

    /**
     * 是否支持长按拖动
     * 默认返回true
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 是否支持侧滑删除
     * 默认返回true
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
```
各个方法的含义与调用时期在注释中已经说得很清楚了，这里就不再重复了
最后只需要在MainActivity里面新建一个ItemTouchHelper并把CallBack传进去，再调用helper的attachToRecyclerView方法绑定recyclerView就可以啦
```
 ItemTouchHelper helper=new ItemTouchHelper(new DragItem(adapter));
 helper.attachToRecyclerView(rv);
```
当然卡片式布局也是可以拖动的


![有图有真相2.gif](http://upload-images.jianshu.io/upload_images/3252031-780b0ea0cd08922c.gif?imageMogr2/auto-orient/strip)
最后附上项目的源代码地址
https://github.com/HelloNanKe/RecyclerViewSwipeDrag
