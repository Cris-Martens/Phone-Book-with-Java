package phonebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

class Person {
    String number;
    String name;

    public int compareTo(String name) {
       return this.name.compareTo(name);
    }

    public int compareTo(Person other) {
        return this.name.compareTo(other.name);
    }
}

public class Main {

    static Duration binarySearchDuration;
    static Duration bubbleSortDuration;
    static Duration jumpSearchDuration;
    static Duration linearSearchDuration;
    static Duration quickSortDuration;
    static Duration hashCreationDuration;
    static Duration hashSearchDuration;
    static Duration total;

    static String foundTime;
    static String sortingTime;
    static String searchTime;
    static String creationTime;

    static List<Person> phoneBook = new ArrayList<>();
    static List<String> findList = new ArrayList<>();
    static Map<String, String> phoneBookHashMap = new HashMap<>();

    static boolean sorted = false;

    static int found = 0;

    private static boolean binarySearch(List<Person> people, String search, int end) {
        int left = 0;
        int right = end;

        while (left <= right) {
            int middle = (left + right) / 2;
            if (people.get(middle).name.compareTo(search) == 0) {
                return true;
            } else if (people.get(middle).name.compareTo(search) > 0) {
                right = middle - 1;
            } else if (people.get(middle).name.compareTo(search) < 0) {
                left = middle + 1;
            }
        }
        return false;
    }

    private static void bubbleSort(List<Person> persons) {
        LocalTime start = LocalTime.now();
        boolean swapped = true;

        while (swapped) {
            swapped = false;
            for (int i = 0; i < persons.size() - 2; i++) {
                if (persons.get(i + 1).compareTo(persons.get(i)) < 0) {
                    swap(i + 1, i);
                    swapped = true;
                }
            }
            if (linearSearchDuration.multipliedBy(10).compareTo(Duration.between(start, LocalTime.now())) <= 0) {
                bubbleSortDuration = Duration.between(start, LocalTime.now());

                return;
            }
        }
        bubbleSortDuration = Duration.between(start, LocalTime.now());

        sorted = true;
    }

    private static void creatHashTable() {
        LocalTime start = LocalTime.now();
        for (Person person : phoneBook) {
            phoneBookHashMap.put(person.name, person.number);
        }

        hashCreationDuration = Duration.between(start, LocalTime.now());
    }

    private static boolean hashSearch(String search) {
        return phoneBookHashMap.containsKey(search);
    }

    private static boolean jumpSearch(List<Person> persons, String name) {

        int currentIndex = 0;
        int previousIndex = 0;
        int length = persons.size();
        int blockSize = (int) Math.floor(Math.sqrt(length));

        while (persons.get(currentIndex).compareTo(name) < 0) {
            previousIndex = currentIndex;
            if (currentIndex + blockSize >= length) {
                currentIndex = blockSize;
            } else {
                currentIndex += blockSize;
            }
        }
        while (persons.get(currentIndex).compareTo(name) > 0) {
            currentIndex--;
            if (currentIndex <=  previousIndex) {
                return false;
            }
        }
        return persons.get(currentIndex).compareTo(name) == 0;
    }

    private static boolean linearSearch(List<Person> persons, String name) {
        for (Person person : persons) {
            if (name.equalsIgnoreCase(person.name)) {
                return true;
            }
        }
        return false;
    }

    private static int partition(List<Person> people, int left, int right) {
        int i = left - 1;
        for (int j = left; j < right; j++) {
            if (people.get(j).compareTo(people.get(right)) < 0) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, right);
        return i + 1;
    }

    private static void quickSort(List<Person> people, int left, int right) {
        if  (left < right) {
            int pivot = partition(people, left, right);
            quickSort(people, left, pivot - 1);
            quickSort(people, pivot + 1, right);
        }
    }

    private static String setString(Duration duration) {
        long Elapsed = duration.toMillis();

        int minutes = (int) Elapsed / 60000;
        int seconds = (int) Elapsed % 60000 / 1000;
        int milliseconds = (int) Elapsed % 1000;

        return String.format("%d min. %d sec. %d ms.", minutes, seconds, milliseconds);
    }

    private static void swap(int one, int two) {
        Person temp = phoneBook.get(one);
        phoneBook.set(one, phoneBook.get(two));
        phoneBook.set(two, temp);
    }

    private static void useBubbleAndJump() {
        System.out.println("Start searching (bubble sort + jump search)...");

        bubbleSort(phoneBook);


        System.out.println("Searching");
        if (sorted) {
            LocalTime startTimeJump = LocalTime.now();
            for (String search : findList) {
                if (jumpSearch(phoneBook, search)) {
                    found++;
                }
            }
            LocalTime endTimeJump = LocalTime.now();
            sortingTime = String.format("Sorting time: %s", setString(bubbleSortDuration));
            jumpSearchDuration = Duration.between(startTimeJump, endTimeJump);
        } else {
            LocalTime startlin =  LocalTime.now();
            for (String search : findList) {
                if (linearSearch(phoneBook, search)) {
                    found++;
                }
            }
            LocalTime endlin =  LocalTime.now();
            sortingTime = String.format("Sorting time: %s - STOPPED Switched to linear search.", setString(bubbleSortDuration));
            linearSearchDuration = Duration.between(startlin, endlin);
        }

        if (jumpSearchDuration != null) {
            searchTime = String.format("Searching time: " + setString(jumpSearchDuration));
            total = jumpSearchDuration.plus(bubbleSortDuration);
        } else {
            total = bubbleSortDuration.plus(linearSearchDuration);
            searchTime = String.format("Searching time: " + setString(linearSearchDuration));
        }
        foundTime = String.format("Found %d / %d entries. Time taken: %s", found, findList.size(), setString(total));
    }

    private static void useHashTable() {
        System.out.println("Start searching (hash table)...");
        creatHashTable();

        LocalTime start =  LocalTime.now();
        for (String search : findList) {
            if (hashSearch(search)) {
                found++;
            }
        }
        hashSearchDuration = Duration.between(start, LocalTime.now());
        total = hashSearchDuration.plus(hashCreationDuration);

        creationTime = String.format("Creating time: %s", setString(hashCreationDuration));
        searchTime = String.format("Searching time: " + setString(hashSearchDuration));
        foundTime = String.format("Found %d / %d entries. Time taken: %s", found, findList.size(), setString(total));
    }

    private static void useLinearSearch() {
        LocalTime start = LocalTime.now();
        System.out.println("Start searching (linear search)...");

        for (String search : findList) {
            if (linearSearch(phoneBook, search)) {
                found++;
            }
        }
        LocalTime end = LocalTime.now();
        linearSearchDuration = Duration.between(start, end);
        total = linearSearchDuration;

        System.out.printf("Found %d / %d entries. Time taken: %s.\n",
                found, findList.size(), setString(linearSearchDuration));
        System.out.println();
    }

    private static void useQuickAndBinary() {
        LocalTime startQuick = LocalTime.now();
        System.out.println("Start searching (quick sort + Binary search)...");
        quickSort(phoneBook, 0, phoneBook.size() - 1);
        LocalTime endQuick = LocalTime.now();
        quickSortDuration = Duration.between(startQuick, endQuick);

        LocalTime startBinarySearch = LocalTime.now();
        for (String search : findList) {
            if (binarySearch(phoneBook, search, phoneBook.size() - 1)) {
                found++;
            }
        }
        LocalTime endBinarySearch = LocalTime.now();
        binarySearchDuration = Duration.between(startBinarySearch, endBinarySearch);

        sortingTime = String.format("Sorting time: %s.",  setString(quickSortDuration));
        searchTime = String.format("Search time: %s.",  setString(binarySearchDuration));
        total = quickSortDuration.plus(binarySearchDuration);

        foundTime = String.format("Found %d / %d entries. Time taken: %s", found, findList.size(), setString(total));
    }

    public static void main(String[] args) {
        File directory = new File("Phone Book with Java/src/Downloads/directory.txt");
        File find = new File("Phone Book with Java/src/Downloads/find.txt");


        // load directory into dynamic array
        try (Scanner scanner = new Scanner(directory)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] information = line.split(" ", 2);
                Person person = new Person();
                person.number = information[0];
                person.name = information[1];
                phoneBook.add(person);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Directory not found at: " + directory.getAbsolutePath());
        }

        // load find into dynamic array
        try (Scanner scanner = new Scanner(find)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                findList.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Find not found at: " + find.getAbsolutePath());
        }


        useLinearSearch();
        found = 0;

        useBubbleAndJump();
        System.out.println(foundTime);
        System.out.println(sortingTime);
        System.out.println(searchTime);
        System.out.println();
        found = 0;

        useQuickAndBinary();
        System.out.println(foundTime);
        System.out.println(sortingTime);
        System.out.println(searchTime);
        System.out.println();
        found = 0;

        useHashTable();
        System.out.println(foundTime);
        System.out.println(creationTime);
        System.out.println(searchTime);
        System.out.println();








    }
}
