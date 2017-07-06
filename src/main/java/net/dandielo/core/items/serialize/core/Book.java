package net.dandielo.core.items.serialize.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.dandielo.core.items.dItem;
import net.dandielo.core.items.serialize.Attribute;
import net.dandielo.core.items.serialize.ItemAttribute;
import net.dandielo.core.utils.NBTItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@Attribute(
   key = "bk",
   name = "Book",
   priority = 45,
   items = {Material.BOOK_AND_QUILL, Material.WRITTEN_BOOK}
)
public class Book extends ItemAttribute {

   private String bookId;
   private String author = null;
   private String title = null;
   private int generation = 0;
   private List pages = new ArrayList();
   private static FileConfiguration books;
   private static File booksFile;


   public Book(dItem item, String key) {
      super(item, key);
   }

   public boolean deserialize(String data) {
      this.bookId = data;
      this.author = books.getString(this.bookId + ".author");
      this.title = books.getString(this.bookId + ".title");
      this.generation = books.getInt(this.bookId + ".generation");
      this.pages.addAll(books.getStringList(this.bookId + ".pages"));
      return true;
   }

   public String serialize() {
      books.set(this.bookId + ".author", this.author);
      books.set(this.bookId + ".title", this.title);
      books.set(this.bookId + ".pages", this.pages);
      books.set(this.bookId + ".generation", Integer.valueOf(this.generation));
      save();
      return this.bookId;
   }

   public ItemStack onNativeAssign(ItemStack item, boolean unused) {
      if(!(item.getItemMeta() instanceof BookMeta)) {
         return item;
      } else {
         BookMeta book = (BookMeta)item.getItemMeta();
         if(item.getType().equals(Material.WRITTEN_BOOK)) {
            book.setAuthor(this.author);
            book.setTitle(this.title);
         }

         book.setPages(this.pages);
         item.setItemMeta(book);
         NBTItemStack helper = new NBTItemStack(item);
         helper.setInt("generation", this.generation);
         return helper.getItemStack();
      }
   }

   public boolean onRefactor(ItemStack item) {
      if(!(item.getItemMeta() instanceof BookMeta)) {
         return false;
      } else {
         BookMeta book = (BookMeta)item.getItemMeta();
         this.author = book.getAuthor();
         this.title = book.getTitle();
         NBTItemStack helper = new NBTItemStack(item);
         this.generation = helper.getInt("generation");
         this.pages.addAll(book.getPages());
         this.bookId = (this.title != null?this.title.replace(" ", "_"):"bookAndQuil" + (new Random()).nextInt(100)) + (new Random()).nextInt(1000);
         return true;
      }
   }

   public static void loadBooks() throws Exception {
      String fileName = "books.yml";
      String filePath = "plugins/dtlTraders";
      File baseDirectory = new File(filePath);
      if(!baseDirectory.exists()) {
         baseDirectory.mkdirs();
      }

      booksFile = new File(filePath, fileName);
      if(!booksFile.exists()) {
         booksFile.createNewFile();
      }

      books = new YamlConfiguration();
      books.load(booksFile);
   }

   public static void save() {
      try {
         books.save(booksFile);
      } catch (IOException var1) {
         ;
      }

   }

   public boolean similar(ItemAttribute that) {
      return this.same(that);
   }

   public boolean same(ItemAttribute thato) {
      if(!(thato instanceof Book)) {
         return false;
      } else {
         Book that = (Book)thato;
         boolean result = true;
         result &= this.author == null?that.author == null:this.author.equals(that.author);
         result &= this.title == null?that.title == null:this.title.equals(that.title);
         result &= this.generation == that.generation;
         result &= this.pages.size() == that.pages.size();

         for(int i = 0; i < this.pages.size() && result; ++i) {
            result &= ((String)this.pages.get(i)).equals(that.pages.get(i));
         }

         return result;
      }
   }

   static {
      try {
         loadBooks();
      } catch (Exception var1) {
         ;
      }

   }
}
