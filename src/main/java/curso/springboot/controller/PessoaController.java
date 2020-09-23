package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repositoy.PessoaRepository;
import curso.springboot.repositoy.TelefoneRepository;

@Controller
public class PessoaController { // Essa é minha classe bean

	@Autowired
	private PessoaRepository pessoaRepository; // instanciei meu dao

	@Autowired
	private TelefoneRepository telefoneRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());

		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoaIt);

		return modelAndView;
	}

	// Metodo salvar
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {

			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");

			Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();

			modelAndView.addObject("pessoas", pessoaIt);

			modelAndView.addObject("pessoaobj", pessoa);

			List<String> msg = new ArrayList<String>();

			for (ObjectError objectError : bindingResult.getAllErrors()) {

				msg.add(objectError.getDefaultMessage());
			}

			modelAndView.addObject("msg", msg);
			return modelAndView;
		}

		pessoaRepository.save(pessoa);

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();

		andView.addObject("pessoas", pessoaIt);

		andView.addObject("pessoaobj", new Pessoa());

		return andView;
	}

	// Metodo listar
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();

		andView.addObject("pessoas", pessoaIt);

		andView.addObject("pessoaobj", new Pessoa());

		return andView;

	}

	// Metodo editar
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

		modelAndView.addObject("pessoaobj", pessoa.get());
		return modelAndView;

	}

	// Metodo excluir
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {

		pessoaRepository.deleteById(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;

	}

	@PostMapping("**/pesquisarpessoa") // Metodo pesquisar
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));

		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
	}

	// Metodo para carregar para telefones
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {

		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return modelAndView;

	}

	@PostMapping("**/addfonePessoa/{pessoaid}") // salvar telefones
	public ModelAndView addfonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {

		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();

		telefone.setPessoa(pessoa);

		if (telefone != null && telefone.getTelefone().isEmpty() || telefone.getTipo().isEmpty()) {

			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");

			modelAndView.addObject("pessoaobj", pessoa);

			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			
			if(telefone.getTelefone().isEmpty()) {
				
				msg.add("Numero deve ser informado");
			}
			if(telefone.getTipo().isEmpty()) {
				
				msg.add("Tipo deve ser informado ");
			}
               modelAndView.addObject("msg", msg);
			
			return modelAndView;

		}

		telefoneRepository.save(telefone);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");

		modelAndView.addObject("pessoaobj", pessoa);

		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));

		return modelAndView;
	}

	// Metodo excluir telefone
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removerTelefone(@PathVariable("idtelefone") Long idtelefone) {

		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();

		telefoneRepository.deleteById(idtelefone);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));

		return modelAndView;

	}

}
