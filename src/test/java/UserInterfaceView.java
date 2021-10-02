import java.io.IOException;
import java.util.Scanner;

public class UserInterfaceView {
    private Controller controller = new Controller();

    public void runInterface() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите название города (анг яз): ");
            System.out.println("Хотите выйти наберите end ");
            String city = scanner.nextLine();
            if (city.matches("[^A-Za-z]+")) {
                System.out.println("Нет такого города");
                continue;
            }
            if (city.matches("end")) {
                System.out.println("До свидания");
                break;
            }

            System.out.println("Введите 1 получить прогноз на 1 день, 5 получить прогноз на 5 дней. Для выхода введите 0:");

            String command = scanner.nextLine();

            if (command.equals("0")) break;

            if (command.equals("1") || command.equals("5")) {
                try {
                    controller.getWeather(command, city);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Неверная команда");
            }
        }
    }
}

