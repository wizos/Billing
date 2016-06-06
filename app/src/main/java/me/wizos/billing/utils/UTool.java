package me.wizos.billing.utils;

/**
 * Created by Wizos on 2016/2/13.
 */
public class UTool {
    // 保留后两位小数
    public static String keep2Float(String num){
        int y,x0,x1,x2;
        char n0,n1,n2;
        String[] nums = num.split("\\.");
        if(nums.length>2){return null;} // 说明该数目含有多位小数点，不予处理
        if(nums.length==1){return null;}// 说明该数目无小数点
        num = nums[1];
        y = Integer.valueOf(nums[0]);
        n0 = nums[1].charAt(0);
        x0 = Integer.parseInt(String.valueOf(n0));
        x1 = 0;
        if( num.length() >= 2 ) {
            n1 = nums[1].charAt(1);
            x1 = Integer.parseInt(String.valueOf(n1));
            if( num.length() >= 3 ) {
                n2 =  nums[1].charAt(2);
                x2 = Integer.parseInt(String.valueOf(n2));
                if( x2 > 5 ){
                    x1 = x1+1;
                    if (x1 > 9){
                        x1=0;
                        x0 = x0 + 1;
                        if (x0 > 9){
                            x0 = 0;
                            y = y + 1;
                        }
                    }
                }
            }
        }
        num = String.valueOf(y) + "." + String.valueOf(x0) + String.valueOf(x1);
        System.out.println("【4】"+ nums[1] +"【】"+ y+"【】"+x0+"【】"+x1+"【】");
        return num;
    }

    public static int decimalNums(String num){
        String[] nums = num.split("\\.");
        return nums[1].length();
    }


    public static int findPosition(String[] array , String string){
        int position = -1;
        int arraySize = array.length;
        for(int i=0;i<arraySize;++i){
            System.out.println("【0】"+i +array[i]+string );
            if(string.equals(array[i])){
                position = i;
                System.out.println("【1】"+i +array[i]+string );
                break;
            }
        }
        return position;
    }

}
