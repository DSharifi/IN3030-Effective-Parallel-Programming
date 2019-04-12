import sys

def run(file_path):
        file = open(file_path, 'r')

        i = 0

        for line in file:
                if i != 0:
                        numbers = line.split(' : ')
                        n = int(numbers[0])
                        factors = numbers[1].split('*')

                        result = 1

                        for factor in factors:
                                result *= int(factor)

                        print(result == n)

                i = 1


if __name__ == '__main__':
        run('Factors_1000.txt')
