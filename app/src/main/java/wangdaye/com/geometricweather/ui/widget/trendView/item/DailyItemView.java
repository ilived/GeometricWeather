package wangdaye.com.geometricweather.ui.widget.trendView.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.ui.image.AbstractIconTarget;
import wangdaye.com.geometricweather.ui.widget.trendView.i.TrendChild;
import wangdaye.com.geometricweather.ui.widget.trendView.i.TrendParent;
import wangdaye.com.geometricweather.utils.DisplayUtils;

/**
 * Daily chartItem item view.
 * */
public class DailyItemView extends ViewGroup implements TrendChild {

    private ChartItemView chartItem;
    private TrendParent trendParent;
    private Paint paint;

    @Nullable private OnClickListener clickListener;

    @Nullable private String weekText;
    @Nullable private String dateText;

    @Nullable private Drawable dayIconDrawable;
    @Nullable private Drawable nightIconDrawable;

    @ColorInt private int contentColor;
    @ColorInt private int subTitleColor;

    private float width;
    private float height;

    private float weekTextBaseLine;

    private float dateTextBaseLine;

    private float dayIconLeft;
    private float dayIconTop;

    private float trendViewTop;

    private float nightIconLeft;
    private float nightIconTop;

    private int iconSize;

    private static final int ICON_SIZE_DIP = 32;
    private static final int TEXT_MARGIN_DIP = 4;
    private static final int ICON_MARGIN_DIP = 8;
    private static final int MARGIN_BOTTOM_DIP = 16;
    private static final int MIN_ITEM_WIDTH = 56;
    private static final int MIN_ITEM_HEIGHT = 144;

    public DailyItemView(Context context) {
        super(context);
        this.initialize();
    }

    public DailyItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public DailyItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DailyItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        setWillNotDraw(false);

        chartItem = new ChartItemView(getContext());
        addView(chartItem);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.content_text_size));
        paint.setTextAlign(Paint.Align.CENTER);

        setTextColor(Color.BLACK, Color.GRAY);

        width = 0;
        height = 0;
        iconSize = (int) DisplayUtils.dpToPx(getContext(), ICON_SIZE_DIP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = Math.max(DisplayUtils.dpToPx(getContext(), MIN_ITEM_WIDTH), width);
        height = Math.max(DisplayUtils.dpToPx(getContext(), MIN_ITEM_HEIGHT), height);

        float y = 0;
        float textMargin = DisplayUtils.dpToPx(getContext(), TEXT_MARGIN_DIP);
        float iconMargin = DisplayUtils.dpToPx(getContext(), ICON_MARGIN_DIP);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        // week text.
        y += textMargin;
        weekTextBaseLine = y - fontMetrics.top;
        y += fontMetrics.bottom - fontMetrics.top;
        y += textMargin;

        // date text.
        y += textMargin;
        dateTextBaseLine = y - fontMetrics.top;
        y += fontMetrics.bottom - fontMetrics.top;
        y += textMargin;

        // day icon.
        y += iconMargin;
        dayIconLeft = (width - iconSize) / 2f;
        dayIconTop = y;
        y += iconSize;
        y += iconMargin;

        // margin bottom.
        float marginBottom = DisplayUtils.dpToPx(getContext(), MARGIN_BOTTOM_DIP);

        // night icon.
        nightIconLeft = (width - iconSize) / 2f;
        nightIconTop = height - marginBottom - iconMargin - iconSize;

        // chartItem item view.
        chartItem.measure(
                MeasureSpec.makeMeasureSpec(
                        (int) width,
                        MeasureSpec.EXACTLY
                ), MeasureSpec.makeMeasureSpec(
                        (int) (nightIconTop - iconMargin - y),
                        MeasureSpec.EXACTLY
                )
        );
        trendViewTop = y;

        setMeasuredDimension((int) width, (int) height);
        if (trendParent != null) {
            trendParent.setDrawingBoundary(
                    (int) (trendViewTop + chartItem.getMarginTop()),
                    (int) (trendViewTop + chartItem.getMeasuredHeight() - chartItem.getMarginBottom())
            );
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        chartItem.layout(
                0,
                (int) trendViewTop,
                chartItem.getMeasuredWidth(),
                (int) trendViewTop + chartItem.getMeasuredHeight()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // week text.
        if (weekText != null) {
            paint.setColor(contentColor);
            canvas.drawText(weekText, getMeasuredWidth() / 2f, weekTextBaseLine, paint);
        }

        // date text.
        if (dateText != null) {
            paint.setColor(subTitleColor);
            canvas.drawText(dateText, getMeasuredWidth() / 2f, dateTextBaseLine, paint);
        }

        int restoreCount;

        // day icon.
        if (dayIconDrawable != null) {
            restoreCount = canvas.save();
            canvas.translate(dayIconLeft, dayIconTop);
            dayIconDrawable.draw(canvas);
            canvas.restoreToCount(restoreCount);
        }

        // night icon.
        if (nightIconDrawable != null) {
            restoreCount = canvas.save();
            canvas.translate(nightIconLeft, nightIconTop);
            nightIconDrawable.draw(canvas);
            canvas.restoreToCount(restoreCount);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (clickListener != null) {
                    clickListener.onClick(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setWeekText(String weekText) {
        this.weekText = weekText;
        invalidate();
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
        invalidate();
    }

    public void setTextColor(@ColorInt int contentColor, @ColorInt int subTitleColor) {
        this.contentColor = contentColor;
        this.subTitleColor = subTitleColor;
        invalidate();
    }

    public void setDayIconDrawable(@Nullable Drawable d) {
        dayIconDrawable = d;
        if (d != null) {
            d.setVisible(true, true);
            d.setCallback(this);
            d.setBounds(0, 0, iconSize, iconSize);
        }
        invalidate();
    }

    public void setNightIconDrawable(@Nullable Drawable d) {
        nightIconDrawable = d;
        if (d != null) {
            d.setVisible(true, true);
            d.setCallback(this);
            d.setBounds(0, 0, iconSize, iconSize);
        }
        invalidate();
    }

    public AbstractIconTarget getDayIconTarget() {
        return new AbstractIconTarget(iconSize) {
            @Override
            public View getTarget() {
                return DailyItemView.this;
            }

            @Override
            public void setDrawableForTarget(Drawable d) {
                setDayIconDrawable(d);
            }

            @Override
            public Drawable getDrawableFromTarget() {
                return dayIconDrawable;
            }

            @Override
            public void setTagForTarget(Object tag) {
                setTag(R.id.tag_icon_day, tag);
            }

            @Override
            public Object getTagFromTarget() {
                return getTag(R.id.tag_icon_day);
            }
        };
    }

    public AbstractIconTarget getNightIconTarget() {
        return new AbstractIconTarget(iconSize) {
            @Override
            public View getTarget() {
                return DailyItemView.this;
            }

            @Override
            public void setDrawableForTarget(Drawable d) {
                setNightIconDrawable(d);
            }

            @Override
            public Drawable getDrawableFromTarget() {
                return nightIconDrawable;
            }

            @Override
            public void setTagForTarget(Object tag) {
                setTag(R.id.tag_icon_night, tag);
            }

            @Override
            public Object getTagFromTarget() {
                return getTag(R.id.tag_icon_night);
            }
        };
    }

    public ChartItemView getTrendItemView() {
        return chartItem;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        clickListener = l;
        super.setOnClickListener(v -> {});
    }

    @Override
    public void setParent(@NonNull TrendParent parent) {
        trendParent = parent;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }
}
