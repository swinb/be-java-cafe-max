package kr.codesqaud.cafe.account;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.codesqaud.cafe.account.form.JoinForm;
import kr.codesqaud.cafe.account.form.LoginForm;
import kr.codesqaud.cafe.account.form.ProfileForm;
import kr.codesqaud.cafe.account.form.ProfileSettingForm;
import kr.codesqaud.cafe.account.form.UsersForm;

@Controller
public class UserController {

	private final UserService userService;
	private final UsersRepository usersRepository;

	public UserController(UserService userService, UsersRepository usersRepository) {
		this.userService = userService;
		this.usersRepository = usersRepository;
	}

	@GetMapping("/users/login")
	public String showLoginPage(Model model, @Nullable @RequestParam boolean errors) {
		model.addAttribute("loginForm", new LoginForm());
		model.addAttribute("errors", errors);
		return "account/login";
	}

	@PostMapping("/users/login")
	public String login(@Valid LoginForm loginForm, Errors errors, RedirectAttributes model) {
		if (errors.hasErrors()) {
			model.addAttribute("errors", true);
			return "redirect:/users/login";
		}
		Optional<User> userOptional = usersRepository.findByEmail(loginForm.getEmail());
		if (userOptional.isEmpty()) {
			model.addAttribute("errors", true);
			return "redirect:/users/login";
		}
		User user = userOptional.get();
		if (user.getPassword().equals(loginForm.getPassword())) {
			model.addAttribute("errors", true);
			return "redirect:/users/login";
		}
		return "redirect:/users/" + user.getId();
	}

	@GetMapping("/users/join")
	public String showJoinPage(Model model) {
		model.addAttribute("joinForm", new JoinForm());
		return "account/join";
	}

	@PostMapping("/users/join")
	public String addUser(@Valid JoinForm joinForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "account/join";
		}
		if (usersRepository.containEmail(joinForm.getEmail())) {
			bindingResult.rejectValue("email", "duplicate","중복된 이메일입니다.");
			return "account/join";
		}
		User user = userService.createNewUser(joinForm);
		return "redirect:/users/" + user.getId();
	}

	@GetMapping("/users")
	public String showUsers(Model model) {
		List<UsersForm> allUsersForm = userService.getAllUsersForm();
		model.addAttribute("users", allUsersForm);
		return "account/members";
	}

	@GetMapping("/users/{userId}")
	public String showUser(Model model, @PathVariable Long userId) {
		Optional<User> userOptional = usersRepository.findById(userId);
		if (userOptional.isEmpty()) {
			return "redirect:/";
		}
		ProfileForm profileForm = userOptional.get().mappingProfileForm();
		model.addAttribute("profileForm", profileForm);
		model.addAttribute("userId", userId);
		return "account/profile";
	}

	@GetMapping("/users/{userId}/update")
	public String showUserProfile(Model model, @PathVariable Long userId, @Nullable @RequestParam boolean errors) {
		Optional<User> userOptional = usersRepository.findById(userId);
		if (userOptional.isEmpty()) {
			return "redirect:/";
		}
		ProfileSettingForm profileSettingForm = userOptional.get().mappingProfileSettingFormWithPassword();
		model.addAttribute("userId", userId);
		model.addAttribute("profileSettingForm", profileSettingForm);
		model.addAttribute("errors", errors);
		return "account/profileUpdate";
	}

	@PutMapping("/users/{userId}/update")
	public String setUserProfile(@Valid ProfileSettingForm profileSettingForm, Errors errors, @PathVariable Long userId,
		RedirectAttributes model) {
		if (errors.hasFieldErrors("email")) {
			model.addAttribute("errors", true);
			return "redirect:/users/" + userId + "/update";
		}
		if (errors.hasFieldErrors("nickName")) {
			model.addAttribute("errors", true);
			return "redirect:/users/" + userId + "/update";
		}
		Optional<User> userOptional = usersRepository.findById(userId);
		if (userOptional.isEmpty()) {
			return "redirect:/";
		}
		if (!userService.checkPasswordByUserId(profileSettingForm.getPassword(), userId)) {
			model.addAttribute("errors", true);
			return "redirect:/users/" + userId + "/update";
		}
		userService.update(profileSettingForm, userId);
		return "redirect:/users/{userId}";
	}
}
