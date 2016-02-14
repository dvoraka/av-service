package dvoraka.avservice.checker


class Cli {

    void run() {

        println("CLI")

        print('> ')
        System.in.eachLine {
            print('> ')
        }
    }

    static void main(def args) {

        Cli cli = new Cli()
        cli.run()
    }
}
