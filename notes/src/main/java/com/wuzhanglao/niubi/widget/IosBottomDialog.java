package com.wuzhanglao.niubi.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuzhanglao.niubi.R;
import com.wuzhanglao.niubi.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 仿IOS风格-----从底部弹出的dialog
 * Created by wuming on 16/10/12.
 */

public class IosBottomDialog extends Dialog {

    private static final String TAG = IosBottomDialog.class.getSimpleName();
    public static final int DEFAULT_PADDING = 8;
    public static final int DEFAULT_TITLE_SIZE = 15;
    public static final int DEFAULT_OPTION_SIZE = 15;

    private LinearLayout options_ll;
    private TextView title;
    private View title_line;
    private IosBottomDialogDismissListener dismissListener;

    private IosBottomDialog(Context context) {
        //给dialog定制了一个主题（透明背景，无边框，无标题栏，浮在Activity上面，模糊）
        super(context);
        setContentView(R.layout.ios_bottom_dialog);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.bottom_dialog_title_tv);
        title_line = findViewById(R.id.bottom_dialog_title_line);
        options_ll = (LinearLayout) findViewById(R.id.options_ll);

        findViewById(R.id.bottom_dialog_cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IosBottomDialog.this.dismiss();
            }
        });
        //点击空白区域可以取消dialog
        this.setCanceledOnTouchOutside(true);
        //点击back键可以取消dialog
        this.setCancelable(true);
        Window window = this.getWindow();
        //让Dialog显示在屏幕的底部
        window.setGravity(Gravity.BOTTOM);
        //设置窗口出现和窗口隐藏的动画
//        window.setWindowAnimations(R.style.ios_bottom_dialog_anim);
        //设置BottomDialog的宽高属性
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }

    private void setIosBottomDialogDismissListener(IosBottomDialogDismissListener listener) {
        this.dismissListener = listener;
    }


    public interface OnOptionClickListener {
        void onOptionClick();
    }

    public interface IosBottomDialogDismissListener {
        void onDismiss();
    }

    public static class Builder {
        private Paraments p;
        private Context context;

        public Builder(Context context) {
            p = new Paraments();
            this.context = context;
        }

        public Builder setTitle(String title, int color) {
            p.title = title;
            p.titleColor = color;
            return this;
        }

        public Builder addOption(String option, int color, OnOptionClickListener listener) {
            p.options.add(new Option(option, color, listener));
            return this;
        }

        public Builder setDialogDismissListener(IosBottomDialogDismissListener listener) {
            p.dismisslistener = listener;
            return this;
        }

        public IosBottomDialog create() {
            final IosBottomDialog dialog = new IosBottomDialog(context);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if (p.title.isEmpty()) {
                //设置标题栏不可见
                dialog.title.setVisibility(View.GONE);
                dialog.title_line.setVisibility(View.GONE);
            } else {
                dialog.title.setText(p.title);
                dialog.title.setTextColor(p.titleColor);
                dialog.title.setTextSize(p.titleSize);
                //设置标题栏可见
                dialog.title.setVisibility(View.VISIBLE);
                dialog.title_line.setVisibility(View.VISIBLE);
            }
            //设置item项点击之后的效果
            if (p.options.size() == 0) {
                dialog.options_ll.setVisibility(View.GONE);
            } else {
                for (int i = 0; i < p.options.size(); i++) {
                    final Option option = p.options.get(i);
                    final TextView optionText = new TextView(context);
                    int padding = UIUtils.dp2px(IosBottomDialog.DEFAULT_PADDING);
                    optionText.setPadding(padding, padding, padding, padding);
                    optionText.setText(option.getName());
                    optionText.setTextSize(p.optionTextSize);
                    optionText.setGravity(Gravity.CENTER);
                    optionText.setTextColor(option.getColor());
                    optionText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (option.getListener() != null) {
                                option.getListener().onOptionClick();
                            }
                        }
                    });
                    dialog.options_ll.addView(optionText);
                    //添加条目之间的分割线
                    if (i != p.options.size() - 1) {
                        View divider = new View(context);
                        divider.setBackgroundResource(R.color.black_444444);
                        dialog.options_ll.addView(divider, params);
                    }
                    //选择到底使用哪个selector文件
                    if (p.options.size() == 1) {
                        if (p.title.isEmpty()) {
                            optionText.setBackgroundResource(R.drawable.bottom_dialog_option13);
                        } else {
                            optionText.setBackgroundResource(R.drawable.bottom_dialog_option3);
                        }
                    } else if (i == 0) {
                        if (p.title.isEmpty()) {
                            optionText.setBackgroundResource(R.drawable.bottom_dialog_option1);
                        } else {
                            optionText.setBackgroundResource(R.drawable.bottom_dialog_option2);
                        }
                    } else if (i < p.options.size() - 1) {
                        optionText.setBackgroundResource(R.drawable.bottom_dialog_option2);
                    } else {
                        optionText.setBackgroundResource(R.drawable.bottom_dialog_option3);
                    }
                }
            }
            //对话框dismiss之后的监听
            dialog.setIosBottomDialogDismissListener(p.dismisslistener);
            return dialog;
        }
    }

    //这个类保存了dialog的众多参数
    private static class Paraments {
        int titleSize;
        int optionTextSize;
        String title;
        int titleColor;
        boolean cancelable;
        List<Option> options;
        IosBottomDialog.IosBottomDialogDismissListener dismisslistener;

        Paraments() {
            title = "";
            titleColor = Color.BLACK;
            cancelable = true;
            options = new ArrayList<>();
            titleSize = DEFAULT_TITLE_SIZE;
            optionTextSize = DEFAULT_OPTION_SIZE;
        }
    }

    private static class Option {
        private String name;
        private int color;
        private IosBottomDialog.OnOptionClickListener listener;

        public Option() {
        }

        Option(String name, int color, IosBottomDialog.OnOptionClickListener listener) {
            this.name = name;
            this.color = color;
            this.listener = listener;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        IosBottomDialog.OnOptionClickListener getListener() {
            return listener;
        }

        public void setListener(IosBottomDialog.OnOptionClickListener listener) {
            this.listener = listener;
        }
    }

}