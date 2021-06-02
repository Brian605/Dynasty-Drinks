package com.returno.dynasty.cart;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CartHelper {
    private static List<Cart> cartList=new ArrayList<>();

    public static void addItemToCart(Cart cart){
        cartList.add(cart);
    }

   public static int getTotalPrices(){
        int total=0;
        for(Cart cart:cartList){
total+=cart.getTotalPrice();
        }
        return total;
   }

    public static boolean isItemInCart(String itemName){
        for (Cart cart:cartList){
            if (cart.getItemName().equals(itemName)){
                return true;
            }
        }
        return false;
    }

    @NonNull
    public static Cart getACartItemByName(String itemName){
        for (Cart cart:cartList){
            if (cart.getItemName().equals(itemName)){
                return cart;
            }
        }
        return cartList.get(0);
    }

    public static void removeItemFromCart(int index){
            if (cartList.get(index).getItemQuantity()>1){
                cartList.get(index).setItemQuantity(cartList.get(index).getItemQuantity()-1);
                return;
        }
            cartList.remove(index);
    }
    public static int getIndexOfCartItem(String name){
        for (int i=0;i<cartList.size();i++){
            if (cartList.get(i).getItemName().equals(name)){
                return i;
            }
        }
        return 0;
    }
    public static void clearCart(){
        cartList.clear();
    }

    public static List<Cart> getCartList() {
        return cartList;
    }

    public static void removeItemFromCartByIndex(int index) {
        cartList.remove(index);
    }

    public static int getCartSize(){
        return cartList.size();
    }

    public static List<String> prepareCart(){
        List<String> strings=new ArrayList<>();
        StringBuilder items=new StringBuilder();
        boolean isFirst=true;
        for (Cart cart:cartList) {
            if (isFirst) {
                items.append(cart.getItemName()).append("(").append(cart.getItemQuantity()).append(")");
                isFirst = false;
            } else
                items.append("__").append(cart.getItemName()).append("(").append(cart.getItemQuantity()).append(")");

        }
        strings.add(items.toString());
        isFirst=true;

StringBuilder prices=new StringBuilder();
        for (Cart cart:cartList) {
            if (isFirst) {
                prices.append(cart.getItemPrice());
                isFirst = false;
            } else
                prices.append("__").append(cart.getItemPrice());

        }
        strings.add(prices.toString());
        isFirst=true;

        StringBuilder categs=new StringBuilder();
        for (Cart cart:cartList) {
            if (isFirst) {
                categs.append(cart.getCategory());
                isFirst = false;
            } else
                categs.append("__").append(cart.getCategory());

        }
        strings.add(categs.toString());


return strings;
    }


}
