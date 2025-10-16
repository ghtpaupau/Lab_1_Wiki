import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите Ваш запрос:");
        String request = scanner.nextLine().trim();
        String standardUrl = "https://ru.wikipedia.org/w/api.php";
        if(WikiSearchResult.makeFullUrl(request, standardUrl) != "") {
            String fullUrl = WikiSearchResult.makeFullUrl(request, standardUrl);
            StringBuilder gsonResponse = WikiSearchResult.httpRequest(fullUrl);
            List<SearchItems> results = WikiSearchResult.gsonParsing(gsonResponse);
            if (results.isEmpty()) {
                System.out.println("По введенному запросу ничего не найдено");
                return;
            } else {
                System.out.println("Найдено результатов: " + results.size());
                System.out.println("Результаты поиска:");

                for (int i = 0; i < results.size(); ++i) {
                    System.out.println((i + 1) + ") " + results.get(i).getTitle());
                    System.out.println();
                }
                System.out.println("Вводите номер выбранной статьи от 1 до " + results.size() + " или 0, если хотите завершить программу");
                while (true) {
                    int chose;
                    if (scanner.hasNextInt()) {
                        chose = scanner.nextInt();
                        if (chose == 0) return;
                        if (chose > results.size() || chose < 1) {
                            System.out.println("Введен некорректый номер статьи, попробуйте снова");
                            continue;
                        }
                    } else {
                        System.out.println("Введен некорректый номер статьи, попробуйте снова");
                        scanner.next();
                        continue;
                    }
                    SearchItems selectedArticle = results.get(chose - 1);
                    String articleUrl = "https://ru.wikipedia.org/w/index.php?curid=" + selectedArticle.getPageid();
                    WikiSearchResult.browsSelectedArticle(articleUrl);
                }
            }
        } else {
            System.out.println("Не удалось получить полную ссылку для заапроса");
            return;
        }
    }
}