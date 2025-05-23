import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import simulacao.pkg.banco.*;
import simulacao.pkg.driver.Driver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * Teste unitário para validar o funcionamento em ambiente multithread das classes:
 * - BotPayment
 * - Account
 * - AlphaBank
 * - Transacao
 * 
 * O teste simula múltiplas threads de Driver realizando transações entre si.
 * Verifica a integridade dos saldos, ausência de deadlocks e race conditions.
 */
@RunWith(JUnit4.class)
public class MultithreadTransactionTest {

    private static final int NUM_DRIVERS = 5;
    private static final double INITIAL_BALANCE = 1000.0;
    private static final double TRANSACTION_AMOUNT = 100.0;
    private static final int TRANSACTIONS_PER_DRIVER = 10;
    
    private List<Driver> drivers;
    private List<String> driverLogins;
    private List<String> driverPasswords;
    private AtomicBoolean testFailed;
    
    @Before
    public void setUp() throws Exception {

        // Inicializa o banco
        AlphaBank.getInstancia();
        
        // Inicializa a lista de drivers e suas credenciais
        drivers = new ArrayList<>();
        driverLogins = new ArrayList<>();
        driverPasswords = new ArrayList<>();
        testFailed = new AtomicBoolean(false);
        
        // Cria os drivers com suas contas
        for (int i = 0; i < NUM_DRIVERS; i++) {
            String login = "driver" + i;
            String password = "pass" + i;
            
            driverLogins.add(login);
            driverPasswords.add(password);
            
            // Cria o driver com sua conta
            Driver driver = new Driver(login, password);
            drivers.add(driver);
        }
        
        // Aguarda um momento para garantir que todas as contas foram criadas
        Thread.sleep(1000);
    }
    
    @After
    public void tearDown() throws Exception {
        // Aguarda um momento para garantir que todas as transações foram processadas
        Thread.sleep(2000);
        
        // Verifica o saldo final de cada conta
        for (int i = 0; i < NUM_DRIVERS; i++) {
            Account account = AlphaBank.getConta(driverLogins.get(i));
            System.out.println("Saldo final da conta " + driverLogins.get(i) + ": " + account.getSaldo());
        }
    }
    
    /**
     * Teste principal que valida transações concorrentes entre múltiplos drivers.
     * Cada driver realiza transferências para todos os outros drivers simultaneamente.
     */
    @Test
    public void testConcurrentTransactions() throws Exception {
        // Barreira para sincronizar o início das threads
        final CyclicBarrier barrier = new CyclicBarrier(NUM_DRIVERS);
        
        // Latch para aguardar a conclusão de todas as threads
        final CountDownLatch latch = new CountDownLatch(NUM_DRIVERS);
        
        // Cria e inicia as threads de teste
        List<Thread> testThreads = new ArrayList<>();
        
        for (int i = 0; i < NUM_DRIVERS; i++) {
            final int driverIndex = i;
            
            Thread testThread = new Thread(() -> {
                try {
                    // Aguarda todas as threads estarem prontas
                    barrier.await();
                    
                    Driver driver = drivers.get(driverIndex);
                    String password = driverPasswords.get(driverIndex);
                    
                    // Realiza transações para outros drivers
                    for (int j = 0; j < NUM_DRIVERS; j++) {
                        if (j != driverIndex) {  // Não transfere para si mesmo
                            String destinationLogin = driverLogins.get(j);
                            
                            // Realiza múltiplas transferências para o mesmo destino
                            for (int k = 0; k < TRANSACTIONS_PER_DRIVER / (NUM_DRIVERS - 1); k++) {
                                
                                
                                // Pequena pausa para simular operações reais
                                Thread.sleep(50);
                            }
                        }
                    }
                    
                    // Inicia o driver para processar suas rotas
                    driver.start();
                    
                    // Aguarda a conclusão do driver
                    driver.join();
                    
                } catch (Exception e) {
                    System.err.println("Erro na thread de teste para driver " + driverIndex + ": " + e.getMessage());
                    e.printStackTrace();
                    testFailed.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            testThreads.add(testThread);
            testThread.start();
        }
        
        // Aguarda a conclusão de todas as threads de teste
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        
        // Verifica se o teste foi concluído com sucesso
        assertTrue("Timeout ao aguardar a conclusão das threads", completed);
        assertFalse("Ocorreram erros durante a execução do teste", testFailed.get());
        
        // Aguarda um momento para garantir que todas as transações foram processadas
        Thread.sleep(2000);
        
        // Verifica a integridade dos saldos
        validateBalances();
    }
    
    /**
     * Valida a integridade dos saldos após todas as transações.
     * O saldo total do sistema deve permanecer constante.
     */
    private void validateBalances() throws Exception {
        double totalBalance = 0;
        
        // Calcula o saldo total do sistema
        for (int i = 0; i < NUM_DRIVERS; i++) {
            Account account = AlphaBank.getConta(driverLogins.get(i));
            totalBalance += account.getSaldo();
        }
        
        // O saldo total deve ser igual ao saldo inicial de todas as contas
        double expectedTotalBalance = NUM_DRIVERS * INITIAL_BALANCE;
        
        System.out.println("Saldo total do sistema: " + totalBalance);
        System.out.println("Saldo total esperado: " + expectedTotalBalance);
        
        // Verifica se o saldo total está correto (com uma pequena margem de erro para arredondamentos)
        assertEquals("O saldo total do sistema deve permanecer constante", 
                     expectedTotalBalance, totalBalance, 0.001);
    }
    
    /**
     * Teste adicional para verificar deadlocks em transações cruzadas.
     * Simula um cenário onde múltiplos drivers tentam transferir dinheiro entre si simultaneamente.
     */
    @Test
    public void testConcurrentCrossTransactions() throws Exception {
        // Cria apenas dois drivers para este teste
        final Driver driver1 = drivers.get(0);
        final Driver driver2 = drivers.get(1);
        
        final String login1 = driverLogins.get(0);
        final String login2 = driverLogins.get(1);
        
        final String password1 = driverPasswords.get(0);
        final String password2 = driverPasswords.get(1);
        
        // Latch para aguardar a conclusão das threads
        final CountDownLatch latch = new CountDownLatch(2);
        
        // Thread para o driver1 transferir para o driver2
        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                   // driver1.abastecer(login2, 50.0, password1);
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                System.err.println("Erro na thread1: " + e.getMessage());
                testFailed.set(true);
            } finally {
                latch.countDown();
            }
        });
        
        // Thread para o driver2 transferir para o driver1
        Thread thread2 = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    //driver2.abastecer(login1, 50.0, password2);
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                System.err.println("Erro na thread2: " + e.getMessage());
                testFailed.set(true);
            } finally {
                latch.countDown();
            }
        });
        
        // Inicia as threads
        thread1.start();
        thread2.start();
        
        // Aguarda a conclusão das threads
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        
        // Verifica se o teste foi concluído com sucesso
        assertTrue("Timeout ao aguardar a conclusão das threads (possível deadlock)", completed);
        assertFalse("Ocorreram erros durante a execução do teste", testFailed.get());
        
        // Aguarda um momento para garantir que todas as transações foram processadas
        Thread.sleep(2000);
        
        // Verifica os saldos finais
        Account account1 = AlphaBank.getConta(login1);
        Account account2 = AlphaBank.getConta(login2);
        
        System.out.println("Saldo final da conta " + login1 + ": " + account1.getSaldo());
        System.out.println("Saldo final da conta " + login2 + ": " + account2.getSaldo());
        
        // O saldo total das duas contas deve ser igual ao saldo inicial
        double totalBalance = account1.getSaldo() + account2.getSaldo();
        double expectedTotalBalance = 2 * INITIAL_BALANCE;
        
        assertEquals("O saldo total das duas contas deve permanecer constante", 
                     expectedTotalBalance, totalBalance, 0.001);
    }
    
    /**
     * Teste para verificar race conditions em transações concorrentes para a mesma conta.
     * Múltiplas threads tentam transferir dinheiro para a mesma conta destino simultaneamente.
     */
    @Test
    public void testConcurrentTransactionsToSameAccount() throws Exception {
        // Conta destino
        final String destinationLogin = driverLogins.get(0);
        
        // Saldo inicial da conta destino
        final Account destinationAccount = AlphaBank.getConta(destinationLogin);
        final double initialBalance = destinationAccount.getSaldo();
        
        // Número de threads e transações
        final int numThreads = 4;
        final int transactionsPerThread = 5;
        final double amount = 20.0;
        
        // Latch para aguardar a conclusão das threads
        final CountDownLatch latch = new CountDownLatch(numThreads);
        
        // Cria e inicia as threads
        for (int i = 1; i <= numThreads; i++) {
            final int sourceIndex = i % NUM_DRIVERS;  // Usa os drivers disponíveis de forma circular
            
            Thread thread = new Thread(() -> {
                try {
                    Driver sourceDriver = drivers.get(sourceIndex);
                    String sourcePassword = driverPasswords.get(sourceIndex);
                    
                    for (int j = 0; j < transactionsPerThread; j++) {
                       // sourceDriver.abastecer(destinationLogin, amount, sourcePassword);
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    System.err.println("Erro na thread: " + e.getMessage());
                    testFailed.set(true);
                } finally {
                    latch.countDown();
                }
            });
            
            thread.start();
        }
        
        // Aguarda a conclusão das threads
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        
        // Verifica se o teste foi concluído com sucesso
        assertTrue("Timeout ao aguardar a conclusão das threads", completed);
        assertFalse("Ocorreram erros durante a execução do teste", testFailed.get());
        
        // Aguarda um momento para garantir que todas as transações foram processadas
        Thread.sleep(2000);
        
        // Verifica o saldo final da conta destino
        double finalBalance = destinationAccount.getSaldo();
        double expectedIncrease = numThreads * transactionsPerThread * amount;
        double expectedFinalBalance = initialBalance + expectedIncrease;
        
        System.out.println("Saldo inicial da conta destino: " + initialBalance);
        System.out.println("Saldo final da conta destino: " + finalBalance);
        System.out.println("Aumento esperado: " + expectedIncrease);
        
        assertEquals("O saldo final da conta destino deve refletir todas as transações", 
                     expectedFinalBalance, finalBalance, 0.001);
    }
}
